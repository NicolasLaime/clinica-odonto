package com.backend_sistema_clinico.especialidad.serviceImpl;

import com.backend_sistema_clinico.especialidad.dto.CreateEspecialidadRequest;
import com.backend_sistema_clinico.especialidad.dto.EspecialidadDTO;
import com.backend_sistema_clinico.especialidad.entity.Especialidad;
import com.backend_sistema_clinico.especialidad.repository.EspecialidadRepository;
import com.backend_sistema_clinico.especialidad.service.EspecialidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository repository;

    @Override
    public List<EspecialidadDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public EspecialidadDTO findById(Long id) {
        Especialidad esp = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));
        return toDTO(esp);
    }

    @Override
    public EspecialidadDTO create(CreateEspecialidadRequest request) {
        if (repository.existsByNombre(request.getNombre())) {
            throw new RuntimeException("Ya existe una especialidad con ese nombre");
        }
        Especialidad esp = Especialidad.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .activo(true)
                .build();
        repository.save(esp);
        return toDTO(esp);
    }

    @Override
    public EspecialidadDTO update(Long id, CreateEspecialidadRequest request) {
        Especialidad esp = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));
        if (request.getNombre() != null) esp.setNombre(request.getNombre());
        if (request.getDescripcion() != null) esp.setDescripcion(request.getDescripcion());
        repository.save(esp);
        return toDTO(esp);
    }

    @Override
    public void delete(Long id) {
        Especialidad esp = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));
        esp.setActivo(false);
        repository.save(esp);
    }

    private EspecialidadDTO toDTO(Especialidad esp) {
        return new EspecialidadDTO(esp.getId(), esp.getNombre(), esp.getDescripcion(), esp.isActivo());
    }



}
