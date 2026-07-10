package com.backend_sistema_clinico.user.serviceImpl;

import com.backend_sistema_clinico.user.dto.ActualizarUsuarioRequest;
import com.backend_sistema_clinico.user.dto.UserDTO;
import com.backend_sistema_clinico.user.entity.Usuario;
import com.backend_sistema_clinico.user.repository.UserRepository;
import com.backend_sistema_clinico.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Usuario save(Usuario user) {
        return userRepository.save(user);
    }



    @Override
    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::toDTO);
    }

    @Override
    public UserDTO findById(Long id) {
        Usuario user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return toDTO(user);
    }

    @Override
    public UserDTO update(Long id, ActualizarUsuarioRequest request) {
        Usuario user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getActive() != null) user.setActive(request.getActive());

        userRepository.save(user);
        return toDTO(user);
    }

    @Override
    public void deactivate(Long id) {
        Usuario user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setActive(false);
        userRepository.save(user);
    }

    private UserDTO toDTO(Usuario user) {
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
