package com.backend_sistema_clinico.paciente.serviceimpl;


import com.backend_sistema_clinico.paciente.dto.CreatePacienteDTO;
import com.backend_sistema_clinico.paciente.service.PacienteService;
import com.backend_sistema_clinico.shared.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.backend_sistema_clinico.especialidad.entity.Especialidad;
import com.backend_sistema_clinico.especialidad.repository.EspecialidadRepository;
import com.backend_sistema_clinico.paciente.dto.PacienteDTO;
import com.backend_sistema_clinico.paciente.entity.Paciente;
import com.backend_sistema_clinico.paciente.repository.PacienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;


@Service
@RequiredArgsConstructor
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final EspecialidadRepository especialidadRepository;

    @Override
    public Page<PacienteDTO> findAll(Pageable pageable) {
        return pacienteRepository
                .findAllByClinicaId(obtenerClinicaId(), pageable)
                .map(this::toDTO);
    }

    @Override
    public PacienteDTO findById(Long id) {
        Paciente p = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        if (!p.getClinicaId().equals(obtenerClinicaId())) {
            throw new RuntimeException("Paciente no encontrado");
        }

        return toDTO(p);
    }

    @Override
    public PacienteDTO create(CreatePacienteDTO request) {
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
                .especialidad(buscarEspecialidad(request.getEspecialidadId()))
                .activo(true)
                .clinicaId(obtenerClinicaId())
                .build();
        pacienteRepository.save(p);
        return toDTO(p);
    }

    @Override
    public PacienteDTO update(Long id, CreatePacienteDTO request) {
        Paciente p = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        if (request.getNombre() != null) p.setNombre(request.getNombre());
        if (request.getApellido() != null) p.setApellido(request.getApellido());
        if (request.getEmail() != null) p.setEmail(request.getEmail());
        if (request.getTelefono() != null) p.setTelefono(request.getTelefono());
        if (request.getDireccion() != null) p.setDireccion(request.getDireccion());
        if (request.getFechaNacimiento() != null) p.setFechaNacimiento(request.getFechaNacimiento());
        if (request.getEspecialidadId() != null) p.setEspecialidad(buscarEspecialidad(request.getEspecialidadId()));
        if (request.getDni() != null) p.setDni(request.getDni());
        if (request.getAlergias() != null) p.setAlergias(request.getAlergias());
        if (request.getEnfermedades() != null) p.setEnfermedades(request.getEnfermedades());
        if (request.getGrupoSanguineo() != null) p.setGrupoSanguineo(request.getGrupoSanguineo());
        if (request.getObservaciones() != null) p.setObservaciones(request.getObservaciones());

        pacienteRepository.save(p);
        return toDTO(p);
    }

    @Override
    public void delete(Long id) {
        Paciente p = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        if (!p.getClinicaId().equals(obtenerClinicaId())) {
            throw new RuntimeException("Paciente no encontrado");
        }

        p.setActivo(false);
        pacienteRepository.save(p);
    }

    private Especialidad buscarEspecialidad(Long id) {
        if (id == null) return null;
        return especialidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));
    }

    private PacienteDTO toDTO(Paciente p) {
        PacienteDTO dto = new PacienteDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setApellido(p.getApellido());
        dto.setEmail(p.getEmail());
        dto.setTelefono(p.getTelefono());
        dto.setDireccion(p.getDireccion());
        dto.setDni(p.getDni());
        dto.setAlergias(p.getAlergias());
        dto.setEnfermedades(p.getEnfermedades());
        dto.setGrupoSanguineo(p.getGrupoSanguineo());
        dto.setObservaciones(p.getObservaciones());
        dto.setFechaNacimiento(p.getFechaNacimiento());
        dto.setActivo(p.isActivo());
        dto.setClinicaId(p.getClinicaId());


        if (p.getEspecialidad() != null) {
            dto.setEspecialidad(new com.backend_sistema_clinico.especialidad.dto.EspecialidadDTO(
                    p.getEspecialidad().getId(),
                    p.getEspecialidad().getNombre(),
                    p.getEspecialidad().getDescripcion(),
                    p.getEspecialidad().isActivo()
            ));
        }

        return dto;
    }

    @Override
    public List<PacienteDTO> buscar(String termino) {
        return pacienteRepository
                .findByClinicaIdAndNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
                        obtenerClinicaId(), termino, termino)
                .stream().map(this::toDTO).toList();
    }

    private String obtenerClinicaId() {
        return SecurityUtils.obtenerClinicaId();
    }


}
