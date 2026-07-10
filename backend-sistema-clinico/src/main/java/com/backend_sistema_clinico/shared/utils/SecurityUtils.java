package com.backend_sistema_clinico.shared.utils;

import com.backend_sistema_clinico.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static String obtenerClinicaId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails user) {
            return user.getClinicaId();
        }
        return null;
    }


}
