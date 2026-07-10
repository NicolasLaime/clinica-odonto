package com.backend_sistema_clinico.turno.serviceImpl;

import com.backend_sistema_clinico.Horarios.entity.Horarios;
import com.backend_sistema_clinico.Horarios.repository.HorarioRepository;
import com.backend_sistema_clinico.especialidad.dto.EspecialidadDTO;
import com.backend_sistema_clinico.especialidad.entity.Especialidad;
import com.backend_sistema_clinico.especialidad.repository.EspecialidadRepository;
import com.backend_sistema_clinico.mercadopago.dto.CreatePreferenceRequest;
import com.backend_sistema_clinico.mercadopago.dto.CreatePreferenceResponse;
import com.backend_sistema_clinico.mercadopago.services.MercadoPagoService;
import com.backend_sistema_clinico.paciente.dto.PacienteDTO;
import com.backend_sistema_clinico.paciente.entity.Paciente;
import com.backend_sistema_clinico.paciente.repository.PacienteRepository;
import com.backend_sistema_clinico.shared.utils.SecurityUtils;
import com.backend_sistema_clinico.turno.dto.*;
import com.backend_sistema_clinico.turno.entity.EstadoTurno;
import com.backend_sistema_clinico.turno.entity.TipoTurno;
import com.backend_sistema_clinico.turno.entity.Turno;
import com.backend_sistema_clinico.turno.entity.TurnoImagen;
import com.backend_sistema_clinico.turno.repository.TurnoImagenRepository;
import com.backend_sistema_clinico.turno.repository.TurnoRepository;
import com.backend_sistema_clinico.turno.service.TurnoService;
import com.backend_sistema_clinico.user.dto.UserDTO;
import com.backend_sistema_clinico.user.entity.Usuario;
import com.backend_sistema_clinico.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.UUID;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TurnoServiceImpl implements TurnoService {

    @Value("${app.upload.dir:uploads/turnos}")
    private String uploadDir;

    @Value("${app.precio-consulta}")
    private Double precioConsulta;

    private final TurnoRepository turnoRepository;
    private final PacienteRepository pacienteRepository;
    private final UserRepository userRepository;
    private final EspecialidadRepository especialidadRepository;
    private final TurnoImagenRepository turnoImagenRepository;
    private final MercadoPagoService mercadoPagoService;
    private final HorarioRepository horarioRepository;




    @Override
    public TurnoDTO create(CreateTurnoRequest request) {
        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Usuario odontologo = userRepository.findById(request.getOdontologoId())
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        Especialidad especialidad = null;
        if (request.getEspecialidadId() != null) {
            especialidad = especialidadRepository.findById(request.getEspecialidadId())
                    .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));
        }

        Turno turno = Turno.builder()
                .paciente(paciente)
                .odontologo(odontologo)
                .especialidad(especialidad)
                .fechaHora(request.getFechaHora())
                .duracionMinutos(request.getDuracionMinutos() != null ? request.getDuracionMinutos() : 30)
                .tipo(request.getTipo())
                .precioTotal(request.getTipo() == TipoTurno.CONSULTA ? precioConsulta : (request.getPrecioTotal() != null ? request.getPrecioTotal() : request.getPrecio()))
                .precio(request.getTipo() == TipoTurno.CONSULTA ? precioConsulta : (request.getPrecioTotal() != null ? request.getPrecioTotal() : request.getPrecio()))
                .senia(request.getSenia())
                .clinicaId(obtenerClinicaId())
                .estado(EstadoTurno.PENDIENTE_PAGO)
                .build();

        LocalDateTime inicioNuevo = turno.getFechaHora();
        LocalDateTime finNuevo = inicioNuevo.plusMinutes(
                turno.getDuracionMinutos() != null ? turno.getDuracionMinutos() : 60);

        List<Turno> existentes = turnoRepository
                .findByOdontologoIdAndFechaHoraBetweenOrderByFechaHoraAsc(
                        turno.getOdontologo().getId(),
                        inicioNuevo.toLocalDate().atStartOfDay(),
                        inicioNuevo.toLocalDate().atTime(LocalTime.MAX))
                .stream()
                .filter(t -> t.getEstado() != EstadoTurno.CANCELADO)
                .toList();

        boolean ocupado = existentes.stream().anyMatch(t -> {
            LocalDateTime tInicio = t.getFechaHora();
            LocalDateTime tFin = tInicio.plusMinutes(
                    t.getDuracionMinutos() != null ? t.getDuracionMinutos() : 60);
            return inicioNuevo.isBefore(tFin) && finNuevo.isAfter(tInicio);
        });

        if (ocupado) {
            throw new RuntimeException("El odontólogo ya tiene un turno en ese horario");
        }

        turnoRepository.save(turno);

        if (turno.getTipo() == TipoTurno.CONSULTA) {
            CreatePreferenceResponse mp = mercadoPagoService.crearPreferencia(
                    new CreatePreferenceRequest(
                            "Consulta - " + turno.getFechaHora().toLocalDate(),
                            turno.getSenia() != null ? turno.getSenia() : turno.getPrecio(),
                            turno.getId()
                    )
            );
            turno.setInitPoint(mp.getInitPoint());
            turno.setPreferenceId(mp.getPreferenceId());
            turnoRepository.save(turno);
        }

        return toDTO(turno);
    }

    @Override
    public TurnoDTO confirmarPago(Long turnoId) {
        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        if (!turno.getClinicaId().equals(obtenerClinicaId())) {
            throw new RuntimeException("Turno no encontrado");
        }
        turno.setEstado(EstadoTurno.CONFIRMADO);
        turnoRepository.save(turno);
        return toDTO(turno);
    }

    @Override
    public TurnoDTO cancelar(Long turnoId) {
        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        if (!turno.getClinicaId().equals(obtenerClinicaId())) {
            throw new RuntimeException("Turno no encontrado");
        }
        turno.setEstado(EstadoTurno.CANCELADO);
        turnoRepository.save(turno);
        return toDTO(turno);
    }

    @Override
    public TurnoDTO completar(Long turnoId, CompletarTurnoRequest request) {
        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        if (!turno.getClinicaId().equals(obtenerClinicaId())) {
            throw new RuntimeException("Turno no encontrado");
        }
        turno.setEstado(EstadoTurno.COMPLETADO);
        turno.setDiagnostico(request.getDiagnostico());
        if (request.getPagoEnConsultorio() != null) {
            turno.setPagoEnConsultorio(request.getPagoEnConsultorio());
        }
        turnoRepository.save(turno);
        return toDTO(turno);
    }

    @Override
    public Page<TurnoDTO> findAll(Pageable pageable) {
        return turnoRepository
                .findAllByClinicaId(obtenerClinicaId(), pageable)
                .map(this::toDTO);
    }

    @Override
    public List<SlotDTO> findDisponibles(LocalDate fecha, Long odontologoId) {
        DayOfWeek dia = fecha.getDayOfWeek();

        List<Horarios> horarios = horarioRepository.findByOdontologoIdAndActivoTrue(odontologoId)
                .stream()
                .filter(h -> h.getDiaSemana() == dia)
                .toList();

        if (horarios.isEmpty()) return List.of();

        List<Turno> ocupados = turnoRepository
                .findByOdontologoIdAndFechaHoraBetweenOrderByFechaHoraAsc(
                        odontologoId,
                        fecha.atStartOfDay(),
                        fecha.atTime(java.time.LocalTime.MAX))
                .stream()
                .filter(t -> t.getEstado() != EstadoTurno.CANCELADO)
                .toList();

        List<SlotDTO> slots = new ArrayList<>();

        for (Horarios horario : horarios) {
            java.time.LocalTime inicio = horario.getHoraInicio();
            java.time.LocalTime fin = horario.getHoraFin();

            while (inicio.isBefore(fin)) {
                LocalDateTime slotDateTime = LocalDateTime.of(fecha, inicio);

                boolean ocupado = ocupados.stream().anyMatch(t ->
                        t.getFechaHora().equals(slotDateTime));

                if (!ocupado) {
                    slots.add(new SlotDTO(slotDateTime, 60));
                }

                inicio = inicio.plusMinutes(60);
            }
        }

        return slots;
    }

    private TurnoDTO toDTO(Turno t) {
        TurnoDTO dto = new TurnoDTO();
        dto.setId(t.getId());

        Paciente p = t.getPaciente();
        PacienteDTO pDTO = new PacienteDTO();
        pDTO.setId(p.getId());
        pDTO.setNombre(p.getNombre());
        pDTO.setApellido(p.getApellido());
        pDTO.setDni(p.getDni());
        pDTO.setEmail(p.getEmail());
        pDTO.setTelefono(p.getTelefono());
        pDTO.setDireccion(p.getDireccion());
        pDTO.setFechaNacimiento(p.getFechaNacimiento());
        pDTO.setActivo(p.isActivo());
        dto.setPaciente(pDTO);
        dto.setInitPoint(t.getInitPoint());
        dto.setPrecio(t.getPrecio());
        dto.setPrecioTotal(t.getPrecioTotal());
        dto.setSenia(t.getSenia());
        dto.setClinicaId(t.getClinicaId());

        Usuario u = t.getOdontologo();
        dto.setOdontologo(new UserDTO(u.getId(), u.getFirstName(), u.getLastName(),
                u.getEmail(), u.getRole(), u.isActive(), u.getClinicaId()));

        if (t.getEspecialidad() != null) {
            Especialidad e = t.getEspecialidad();
            dto.setEspecialidad(new EspecialidadDTO(e.getId(), e.getNombre(), e.getDescripcion(), e.isActivo()));
        }

        dto.setFechaHora(t.getFechaHora());
        dto.setDuracionMinutos(t.getDuracionMinutos());
        dto.setTipo(t.getTipo());
        dto.setEstado(t.getEstado());
        dto.setDiagnostico(t.getDiagnostico());
        dto.setLinkPago(t.getLinkPago());
        dto.setCallbackUrl(t.getCallbackUrl());
        dto.setImagenes(t.getImagenes().stream().map(img -> {
            TurnoImagenDTO imgDTO = new TurnoImagenDTO();
            imgDTO.setId(img.getId());
            imgDTO.setNombreArchivo(img.getNombreArchivo());
            imgDTO.setUrl("/api/v1/turnos/imagenes/" + img.getId());
            imgDTO.setTipoContenido(img.getTipoContenido());
            return imgDTO;
        }).toList());

        return dto;
    }

    @Override
    public List<TurnoDTO> findByPacienteId(Long pacienteId) {
        return turnoRepository
                .findByPacienteIdAndClinicaIdOrderByFechaHoraDesc(pacienteId, obtenerClinicaId())
                .stream().map(this::toDTO).toList();
    }

    @Override
    public TurnoDTO subirImagen(Long turnoId, MultipartFile archivo) {
        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        if (!turno.getClinicaId().equals(obtenerClinicaId())) {
            throw new RuntimeException("Turno no encontrado");
        }

        if (archivo.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }

        try {
            String nombreUnico = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
            Path rutaCompleta = Paths.get(uploadDir, nombreUnico);
            Files.createDirectories(rutaCompleta.getParent());
            Files.copy(archivo.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

            TurnoImagen imagen = TurnoImagen.builder()
                    .turno(turno)
                    .nombreArchivo(archivo.getOriginalFilename())
                    .rutaArchivo(rutaCompleta.toString())
                    .tipoContenido(archivo.getContentType())
                    .build();

            turnoImagenRepository.save(imagen);
            return toDTO(turno);

        } catch (IOException e) {
            throw new RuntimeException("Error al subir la imagen: " + e.getMessage());
        }
    }

    @Override
    public byte[] obtenerImagen(Long imagenId) {
        TurnoImagen imagen = turnoImagenRepository.findById(imagenId)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));

        try {
            Path ruta = Paths.get(imagen.getRutaArchivo());
            Resource resource = new UrlResource(ruta.toUri());
            if (!resource.exists()) {
                throw new RuntimeException("El archivo no existe en el servidor");
            }
            return Files.readAllBytes(ruta);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer la imagen: " + e.getMessage());
        }
    }

    @Override
    public TurnoDTO findById(Long id) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        if (!turno.getClinicaId().equals(obtenerClinicaId())) {
            throw new RuntimeException("Turno no encontrado");
        }
        return toDTO(turno);
    }

    private String obtenerClinicaId() {
        return SecurityUtils.obtenerClinicaId();
    }


}
