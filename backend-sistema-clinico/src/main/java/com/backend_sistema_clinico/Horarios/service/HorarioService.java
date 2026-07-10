package com.backend_sistema_clinico.Horarios.service;

import com.backend_sistema_clinico.Horarios.Dto.CreateHorarioRequest;
import com.backend_sistema_clinico.Horarios.Dto.HorarioDTO;

import java.time.DayOfWeek;
import java.util.List;

public interface HorarioService {

    HorarioDTO create(CreateHorarioRequest request);
    List<HorarioDTO> findByOdontologo(Long odontologoId);
    List<HorarioDTO> findByDia(DayOfWeek dia);
    void delete(Long id);

}
