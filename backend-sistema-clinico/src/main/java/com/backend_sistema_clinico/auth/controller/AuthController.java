package com.backend_sistema_clinico.auth.controller;

import com.backend_sistema_clinico.auth.dto.AuthResponse;
import com.backend_sistema_clinico.auth.dto.LoginRequest;
import com.backend_sistema_clinico.auth.dto.RefreshTokenRequest;
import com.backend_sistema_clinico.auth.dto.RegisterRequest;
import com.backend_sistema_clinico.auth.service.AuthService;
import com.backend_sistema_clinico.user.dto.UserDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> me() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }
}
