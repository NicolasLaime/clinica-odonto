package com.backend_sistema_clinico.mensaje.controller;

import com.backend_sistema_clinico.mensaje.dto.*;
import com.backend_sistema_clinico.mensaje.service.MensajeService;
import com.backend_sistema_clinico.shared.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MensajeController {

    private final MensajeService service;

    @GetMapping("/api/v1/mensajes")
    @PreAuthorize("hasAnyRole('ADMIN_SAAS', 'ODONTOLOGO')")
    public ResponseEntity<List<MensajeDTO>> listar() {
        return ResponseEntity.ok(service.listar(SecurityUtils.obtenerClinicaId()));
    }

    @GetMapping("/api/v1/mensajes/{telefono}")
    @PreAuthorize("hasAnyRole('ADMIN_SAAS', 'ODONTOLOGO')")
    public ResponseEntity<List<MensajeDTO>> listarPorTelefono(@PathVariable String telefono) {
        var clinicaId = SecurityUtils.obtenerClinicaId();
        service.marcarComoLeidos(clinicaId, telefono);
        return ResponseEntity.ok(service.listarPorTelefono(clinicaId, telefono));
    }

    @PostMapping("/api/v1/mensajes/{telefono}")
    @PreAuthorize("hasAnyRole('ADMIN_SAAS', 'ODONTOLOGO')")
    public ResponseEntity<MensajeDTO> enviar(@PathVariable String telefono,
                                              @RequestBody EnviarMensajeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.enviar(SecurityUtils.obtenerClinicaId(), telefono, request));
    }

    @PostMapping("/api/v1/mensajes/guardar")
    public ResponseEntity<MensajeDTO> guardar(HttpServletRequest request,
                                               @RequestBody GuardarMensajeRequest body) {
        var clinicaId = (String) request.getAttribute("clinicaId");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.guardar(clinicaId, body));
    }
}
