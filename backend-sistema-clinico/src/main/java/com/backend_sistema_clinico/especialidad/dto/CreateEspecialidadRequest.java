package com.backend_sistema_clinico.especialidad.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEspecialidadRequest {

    @NotBlank
    private String nombre;
    private String descripcion;

}
