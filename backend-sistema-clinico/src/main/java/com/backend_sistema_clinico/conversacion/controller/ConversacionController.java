package com.backend_sistema_clinico.conversacion.controller;
import com.backend_sistema_clinico.conversacion.dto.ConversacionDto;
import com.backend_sistema_clinico.conversacion.dto.UpdateConversacionRequest;
import com.backend_sistema_clinico.conversacion.service.ConversacionService;
import com.backend_sistema_clinico.shared.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ConversacionController {

    private final ConversacionService service;

    // ===== n8n (x-api-key) =====

    @GetMapping("/api/v1/conversaciones/{telefono}")
    public ResponseEntity<ConversacionDto> obtener(
            @PathVariable String telefono,
            @RequestAttribute("clinicaId") String clinicaId) {
        return ResponseEntity.ok(service.obtener(telefono, clinicaId));
    }

    @PatchMapping("/api/v1/conversaciones/{telefono}")
    public ResponseEntity<ConversacionDto> actualizar(
            @PathVariable String telefono,
            @RequestBody UpdateConversacionRequest request,
            @RequestAttribute("clinicaId") String clinicaId) {
        return ResponseEntity.ok(service.actualizar(telefono, request, clinicaId));
    }

    @DeleteMapping("/api/v1/conversaciones/{telefono}")
    public ResponseEntity<Void> eliminar(
            @PathVariable String telefono,
            @RequestAttribute("clinicaId") String clinicaId) {
        service.eliminar(telefono, clinicaId);
        return ResponseEntity.noContent().build();
    }

    // ===== CRM (JWT) =====

    @GetMapping("/api/v1/crm/conversaciones")
    public ResponseEntity<List<ConversacionDto>> listar() {
        return ResponseEntity.ok(service.listar(SecurityUtils.obtenerClinicaId()));
    }

    @GetMapping("/api/v1/crm/conversaciones/{telefono}")
    public ResponseEntity<ConversacionDto> obtenerCrm(@PathVariable String telefono) {
        return ResponseEntity.ok(service.obtener(telefono, SecurityUtils.obtenerClinicaId()));
    }




}
