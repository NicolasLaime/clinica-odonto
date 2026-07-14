package com.backend_sistema_clinico.user.service;

import com.backend_sistema_clinico.user.dto.ActualizarUsuarioRequest;
import com.backend_sistema_clinico.user.dto.CrearUsuarioRequest;
import com.backend_sistema_clinico.user.dto.UserDTO;
import com.backend_sistema_clinico.user.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    Usuario save(Usuario user);
    Page<UserDTO> findAll(Pageable pageable);
    UserDTO findById(Long id);
    UserDTO update(Long id, ActualizarUsuarioRequest request);
    void deactivate(Long id);
    UserDTO crear(CrearUsuarioRequest request);


}
