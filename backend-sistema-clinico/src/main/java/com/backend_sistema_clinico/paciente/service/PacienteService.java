package com.backend_sistema_clinico.paciente.service;

import com.backend_sistema_clinico.paciente.dto.CreatePacienteDTO;
import com.backend_sistema_clinico.paciente.dto.PacienteDTO;
import com.backend_sistema_clinico.paciente.dto.PacienteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PacienteService {

    Page<PacienteDTO> findAll(Pageable pageable);
    PacienteDTO findById(Long id);
    PacienteDTO create(CreatePacienteDTO request);
    PacienteDTO update(Long id, CreatePacienteDTO request);
    void delete(Long id);
    List<PacienteDTO> buscar(String termino);


}
