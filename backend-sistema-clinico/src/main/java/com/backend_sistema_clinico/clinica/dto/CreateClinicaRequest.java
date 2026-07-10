package com.backend_sistema_clinico.clinica.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class CreateClinicaRequest {

    @NotBlank
    private String nombre;

    @NotBlank @Email
    private String email;

}
