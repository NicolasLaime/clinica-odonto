package com.backend_sistema_clinico.Horarios.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;


@Data @NoArgsConstructor @AllArgsConstructor
public class CreateHorarioRequest {

    @NotNull
    private Long odontologoId;

    @NotNull
    private DayOfWeek diaSemana;

    @NotNull
    private LocalTime horaInicio;

    @NotNull
    private LocalTime horaFin;

}
