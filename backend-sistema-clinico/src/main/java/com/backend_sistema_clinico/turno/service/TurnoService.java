package com.backend_sistema_clinico.turno.service;

import com.backend_sistema_clinico.turno.dto.CompletarTurnoRequest;
import com.backend_sistema_clinico.turno.dto.CreateTurnoRequest;
import com.backend_sistema_clinico.turno.dto.SlotDTO;
import com.backend_sistema_clinico.turno.dto.TurnoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface TurnoService {
    TurnoDTO create(CreateTurnoRequest request);
    TurnoDTO confirmarPago(Long turnoId);
    TurnoDTO findById(Long id);
    TurnoDTO cancelar(Long turnoId);
    TurnoDTO completar(Long turnoId, CompletarTurnoRequest request);
    Page<TurnoDTO> findAll(Pageable pageable);
    List<SlotDTO> findDisponibles(LocalDate fecha, Long odontologoId);
    List<TurnoDTO> findByPacienteId(Long pacienteId);
    TurnoDTO subirImagen(Long turnoId, MultipartFile archivo);
    byte[] obtenerImagen(Long imagenId);

}
