package com.backend_sistema_clinico.especialidad.service;

import com.backend_sistema_clinico.especialidad.dto.CreateEspecialidadRequest;
import com.backend_sistema_clinico.especialidad.dto.EspecialidadDTO;

import java.util.List;

public interface EspecialidadService {


    List<EspecialidadDTO> findAll();
    EspecialidadDTO findById(Long id);
    EspecialidadDTO create(CreateEspecialidadRequest request);
    EspecialidadDTO update(Long id, CreateEspecialidadRequest request);
    void delete(Long id);


}
