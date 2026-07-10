package com.backend_sistema_clinico.user.dto;


import com.backend_sistema_clinico.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarUsuarioRequest {

    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private Boolean active;
    private String clinicaId;



}
