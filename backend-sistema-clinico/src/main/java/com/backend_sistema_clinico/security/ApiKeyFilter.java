package com.backend_sistema_clinico.security;

import org.springframework.web.filter.OncePerRequestFilter;
import com.backend_sistema_clinico.clinica.service.ClinicaService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ApiKeyFilter extends OncePerRequestFilter {

    private final ClinicaService clinicaService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String path = request.getRequestURI();

        // Solo valida apiKey en rutas de n8n y conversaciones
        if (path.startsWith("/api/v1/n8n/") || path.startsWith("/api/v1/conversaciones/")) {
            String apiKey = request.getHeader("x-api-key");

            if (apiKey == null || apiKey.isBlank()) {
                response.setStatus(401);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"API Key requerida\"}");
                return;
            }

            try {
                var clinica = clinicaService.buscarPorApiKey(apiKey);
                request.setAttribute("clinicaId", clinica.getId());
            } catch (Exception e) {
                response.setStatus(401);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"API Key inválida\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }

}
