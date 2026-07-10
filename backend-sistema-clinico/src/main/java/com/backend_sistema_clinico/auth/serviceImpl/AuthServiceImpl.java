package com.backend_sistema_clinico.auth.serviceImpl;

import com.backend_sistema_clinico.auth.dto.AuthResponse;
import com.backend_sistema_clinico.auth.dto.LoginRequest;
import com.backend_sistema_clinico.auth.dto.RefreshTokenRequest;
import com.backend_sistema_clinico.auth.dto.RegisterRequest;
import com.backend_sistema_clinico.auth.mapper.AuthMapper;
import com.backend_sistema_clinico.auth.service.AuthService;
import com.backend_sistema_clinico.security.CustomUserDetails;
import com.backend_sistema_clinico.security.jwt.JwtService;
import com.backend_sistema_clinico.user.dto.UserDTO;
import com.backend_sistema_clinico.user.entity.Role;
import com.backend_sistema_clinico.user.entity.Usuario;
import com.backend_sistema_clinico.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthMapper authMapper;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (request.getRole() == Role.ODONTOLOGO && request.getClinicaId() == null) {
            throw new RuntimeException("ODONTOLOGO requiere clinicaId");
        }

        Usuario user = Usuario.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .clinicaId(request.getClinicaId())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .build();

        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {
        var decoded = jwtService.validateToken(request.getRefreshToken());
        Long userId = Long.valueOf(decoded.getSubject());

        Usuario user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public UserDTO getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        Usuario user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return authMapper.toUserDTO(user);
    }
}
