package com.backend_sistema_clinico.turno.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompletarTurnoRequest {

    private String diagnostico;

    private Double pagoEnConsultorio;

}
