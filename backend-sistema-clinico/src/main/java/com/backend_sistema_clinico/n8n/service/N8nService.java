package com.backend_sistema_clinico.n8n.service;

import com.backend_sistema_clinico.mercadopago.dto.CreatePreferenceRequest;
import com.backend_sistema_clinico.mercadopago.dto.CreatePreferenceResponse;
import com.backend_sistema_clinico.mercadopago.services.MercadoPagoService;
import com.backend_sistema_clinico.n8n.dto.CreateTurnoN8nRequest;
import com.backend_sistema_clinico.n8n.dto.TurnoN8NResponse;
import com.backend_sistema_clinico.paciente.dto.CreatePacienteDTO;
import com.backend_sistema_clinico.paciente.dto.PacienteDTO;
import com.backend_sistema_clinico.paciente.entity.Paciente;
import com.backend_sistema_clinico.paciente.repository.PacienteRepository;
import com.backend_sistema_clinico.turno.entity.EstadoTurno;
import com.backend_sistema_clinico.turno.entity.TipoTurno;
import com.backend_sistema_clinico.turno.entity.Turno;
import com.backend_sistema_clinico.turno.repository.TurnoRepository;
import com.backend_sistema_clinico.user.entity.Usuario;
import com.backend_sistema_clinico.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class N8nService {

    @Value("${app.precio-consulta}")
    private Double precioConsulta;

    @Value("${app.senia}")
    private Double senia;

    private final PacienteRepository pacienteRepository;
    private final UserRepository userRepository;
    private final TurnoRepository turnoRepository;
    private final MercadoPagoService mercadoPagoService;
    private final RestTemplate restTemplate;

    public TurnoN8NResponse crearTurno(CreateTurnoN8nRequest request) {
        Paciente paciente;
        if (request.getPacienteId() != null) {
            paciente = pacienteRepository.findById(request.getPacienteId())
                    .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        } else {
            paciente = pacienteRepository.findByTelefono(request.getTelefono())
                    .orElseGet(() -> pacienteRepository.save(
                            Paciente.builder()
                                    .nombre(request.getNombre())
                                    .apellido(request.getApellido() != null ? request.getApellido() : "")
                                    .telefono(request.getTelefono())
                                    .clinicaId(request.getClinicaId())
                                    .activo(true)
                                    .build()
                    ));
        }

        Usuario odontologo = userRepository.findById(request.getOdontologoId())
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        boolean ocupado = turnoRepository
                .findByOdontologoIdAndFechaHoraBetweenOrderByFechaHoraAsc(
                        request.getOdontologoId(),
                        request.getFechaHora().toLocalDate().atStartOfDay(),
                        request.getFechaHora().toLocalDate().atTime(LocalTime.MAX))
                .stream()
                .anyMatch(t -> t.getEstado() != EstadoTurno.CANCELADO &&
                        request.getFechaHora().isBefore(
                                t.getFechaHora().plusMinutes(t.getDuracionMinutos() != null ? t.getDuracionMinutos() : 60)) &&
                        request.getFechaHora().plusMinutes(60).isAfter(t.getFechaHora()));

        if (ocupado) {
            throw new RuntimeException("El odontólogo ya tiene un turno en ese horario");
        }

        Turno turno = Turno.builder()
                .paciente(paciente)
                .odontologo(odontologo)
                .fechaHora(request.getFechaHora())
                .duracionMinutos(60)
                .tipo(TipoTurno.CONSULTA)
                .precioTotal(precioConsulta)
                .precio(precioConsulta)
                .senia(senia)
                .clinicaId(request.getClinicaId())
                .estado(EstadoTurno.PENDIENTE_PAGO)
                .callbackUrl(request.getCallbackUrl())
                .build();

        turnoRepository.save(turno);

        CreatePreferenceResponse mp = mercadoPagoService.crearPreferencia(
                new CreatePreferenceRequest(
                        "Consulta - " + turno.getFechaHora().toLocalDate(),
                        turno.getSenia(),
                        turno.getId()
                )
        );

        turno.setInitPoint(mp.getInitPoint());
        turno.setPreferenceId(mp.getPreferenceId());
        turnoRepository.save(turno);

        return new TurnoN8NResponse(turno.getId(), mp.getInitPoint(), "PENDIENTE_PAGO");
    }

    public PacienteDTO crearPaciente(CreatePacienteDTO request, String clinicaId) {
        Paciente p = Paciente.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .fechaNacimiento(request.getFechaNacimiento())
                .dni(request.getDni())
                .alergias(request.getAlergias())
                .enfermedades(request.getEnfermedades())
                .grupoSanguineo(request.getGrupoSanguineo())
                .observaciones(request.getObservaciones())
                .activo(true)
                .clinicaId(clinicaId)
                .build();
        pacienteRepository.save(p);
        return new PacienteDTO(
                p.getId(), p.getNombre(), p.getApellido(), p.getDni(),
                p.getEmail(), p.getTelefono(), p.getDireccion(),
                p.getFechaNacimiento(), null, p.getAlergias(),
                p.getEnfermedades(), p.getGrupoSanguineo(),
                p.getObservaciones(), p.isActivo(), p.getClinicaId()
        );
    }

    public PacienteDTO buscarPorDni(String dni, String clinicaId) {
        return pacienteRepository.findByDniAndClinicaId(dni, clinicaId)
                .map(p -> new PacienteDTO(
                        p.getId(),
                        p.getNombre(),
                        p.getApellido(),
                        p.getDni(),
                        p.getEmail(),
                        p.getTelefono(),
                        p.getDireccion(),
                        p.getFechaNacimiento(),
                        null,
                        p.getAlergias(),
                        p.getEnfermedades(),
                        p.getGrupoSanguineo(),
                        p.getObservaciones(),
                        p.isActivo(),
                        p.getClinicaId()
                ))
                .orElse(null);
    }

}
