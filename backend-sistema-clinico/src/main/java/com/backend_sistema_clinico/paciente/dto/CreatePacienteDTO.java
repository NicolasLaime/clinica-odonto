package com.backend_sistema_clinico.paciente.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data @NoArgsConstructor @AllArgsConstructor
public class CreatePacienteDTO {

    @NotBlank
    private String nombre;
    @NotBlank
    private String apellido;
    @NotBlank
    private String dni;
    private String email;
    @NotBlank
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;
    private Long especialidadId;
    private String alergias;
    private String enfermedades;
    private String grupoSanguineo;
    private String observaciones;
    private String clinicaId;


}
