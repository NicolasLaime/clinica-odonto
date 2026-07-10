package com.backend_sistema_clinico.auth.service;

import com.backend_sistema_clinico.auth.dto.AuthResponse;
import com.backend_sistema_clinico.auth.dto.LoginRequest;
import com.backend_sistema_clinico.auth.dto.RefreshTokenRequest;
import com.backend_sistema_clinico.auth.dto.RegisterRequest;
import com.backend_sistema_clinico.user.dto.UserDTO;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(RefreshTokenRequest request);
    UserDTO getCurrentUser();
}
