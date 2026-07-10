package com.backend_sistema_clinico.especialidad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspecialidadDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private boolean activo;

}
