package com.backend_sistema_clinico.config;

import com.backend_sistema_clinico.security.ApiKeyFilter;
import com.backend_sistema_clinico.security.CustomUserDetailsService;
import com.backend_sistema_clinico.security.jwt.JWTAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTAuthenticationFilter jwtFilter;
    private final ApiKeyFilter apiKeyFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    // Público
                    .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/refresh").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers("/api/mercadopago/**").permitAll()
                    .requestMatchers("/api/v1/n8n/**").permitAll()
                    .requestMatchers("/api/v1/conversaciones/**").permitAll()




                    // Users - solo ADMIN_SAAS
                    .requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN_SAAS")
                    .requestMatchers(HttpMethod.POST, "/api/v1/users").hasRole("ADMIN_SAAS")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/users/**").hasRole("ADMIN_SAAS")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasRole("ADMIN_SAAS")

                    // Test
                    .requestMatchers(HttpMethod.GET, "/api/v1/test/admin").hasRole("ADMIN_SAAS")
                    .requestMatchers(HttpMethod.GET, "/api/v1/test/odontologo").hasAnyRole("ADMIN_SAAS", "ODONTOLOGO")

                    // Auth - autenticado
                    .requestMatchers("/api/v1/auth/**").authenticated()

                    // Especialidades
                    .requestMatchers(HttpMethod.GET, "/api/v1/especialidades/**").hasAnyRole("ADMIN_SAAS", "ODONTOLOGO")
                    .requestMatchers(HttpMethod.POST, "/api/v1/especialidades").hasRole("ADMIN_SAAS")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/especialidades/**").hasRole("ADMIN_SAAS")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/especialidades/**").hasRole("ADMIN_SAAS")

                    // Pacientes
                    .requestMatchers(HttpMethod.GET, "/api/v1/pacientes/**").hasAnyRole("ADMIN_SAAS", "ODONTOLOGO")
                    .requestMatchers(HttpMethod.POST, "/api/v1/pacientes").hasAnyRole("ADMIN_SAAS", "ODONTOLOGO")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/pacientes/**").hasAnyRole("ADMIN_SAAS", "ODONTOLOGO")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/pacientes/**").hasRole("ADMIN_SAAS")

                    // Turnos
                    .requestMatchers(HttpMethod.POST, "/api/v1/turnos").hasAnyRole("ADMIN_SAAS", "ODONTOLOGO")
                    .requestMatchers(HttpMethod.GET, "/api/v1/turnos/**").hasAnyRole("ADMIN_SAAS", "ODONTOLOGO")
                    .requestMatchers(HttpMethod.POST, "/api/v1/turnos/{id}/confirmar-pago").hasRole("ADMIN_SAAS")
                    .requestMatchers(HttpMethod.POST, "/api/v1/turnos/{id}/cancelar").hasAnyRole("ADMIN_SAAS", "ODONTOLOGO")
                    .requestMatchers(HttpMethod.POST, "/api/v1/turnos/{id}/completar").hasAnyRole("ADMIN_SAAS", "ODONTOLOGO")
                    .requestMatchers("/api/v1/turnos/imagenes/**").hasAnyRole("ADMIN_SAAS", "ODONTOLOGO")

                    // Horarios
                    .requestMatchers(HttpMethod.POST, "/api/v1/horarios").hasAnyRole("ADMIN_SAAS", "ODONTOLOGO")
                    .requestMatchers(HttpMethod.GET, "/api/v1/horarios/**").hasAnyRole("ADMIN_SAAS", "ODONTOLOGO")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/horarios/**").hasRole("ADMIN_SAAS")

                    //Crm-Conversaciones
                    .requestMatchers(HttpMethod.GET, "/api/v1/crm/conversaciones/**").hasAnyRole("ADMIN_SAAS", "ODONTOLOGO")

                    // Dashboard - reportes
                    .requestMatchers(HttpMethod.GET, "/api/v1/reportes/**").hasAnyRole("ADMIN_SAAS", "ODONTOLOGO")

                    // Mensajes
                    .requestMatchers(HttpMethod.POST, "/api/v1/mensajes/guardar").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/mensajes/**").hasAnyRole("ADMIN_SAAS", "ODONTOLOGO")
                    .requestMatchers(HttpMethod.POST, "/api/v1/mensajes/**").hasAnyRole("ADMIN_SAAS", "ODONTOLOGO")

                    //crear clinica
                    .requestMatchers(HttpMethod.POST, "/api/v1/clinicas").hasRole("ADMIN_SAAS")

                    .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
