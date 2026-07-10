package com.backend_sistema_clinico.auth.mapper;

import com.backend_sistema_clinico.user.dto.UserDTO;
import com.backend_sistema_clinico.user.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public UserDTO toUserDTO(Usuario user) {
        return new UserDTO(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRole(),
            user.isActive(),
                user.getClinicaId()
        );
    }
}
