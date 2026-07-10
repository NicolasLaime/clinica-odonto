package com.backend_sistema_clinico.clinica.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ClinicaDto {

    private String id;
    private String nombre;
    private String email;
    private String apiKey;
    private boolean activo;

}
