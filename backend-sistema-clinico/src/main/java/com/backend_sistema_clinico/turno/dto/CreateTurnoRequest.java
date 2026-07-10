package com.backend_sistema_clinico.turno.dto;

import com.backend_sistema_clinico.turno.entity.TipoTurno;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class CreateTurnoRequest {
    @NotNull
    private Long pacienteId;

    @NotNull
    private Long odontologoId;

    private Long especialidadId;

    @NotNull
    private LocalDateTime fechaHora;

    private Integer duracionMinutos;

    @NotNull
    private Double precio;

    private Double precioTotal;

    private Double senia;

    private String clinicaId;

    @NotNull
    private TipoTurno tipo;
}
