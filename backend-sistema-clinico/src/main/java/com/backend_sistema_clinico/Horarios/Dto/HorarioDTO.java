package com.backend_sistema_clinico.Horarios.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class HorarioDTO {

    private Long id;
    private Long odontologoId;
    private String odontologoNombre;
    private DayOfWeek diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private boolean activo;



}
