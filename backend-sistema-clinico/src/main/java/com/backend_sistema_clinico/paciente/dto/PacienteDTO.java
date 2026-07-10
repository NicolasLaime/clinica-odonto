package com.backend_sistema_clinico.paciente.dto;
import com.backend_sistema_clinico.especialidad.dto.EspecialidadDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;
    private EspecialidadDTO especialidad;
    private String alergias;
    private String enfermedades;
    private String grupoSanguineo;
    private String observaciones;
    private boolean activo;
    private String clinicaId;



}
