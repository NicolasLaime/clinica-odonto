package com.backend_sistema_clinico.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping("/admin")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("Acceso concedido: eres ADMIN_SAAS");
    }

    @GetMapping("/odontologo")
    public ResponseEntity<String> odontologoOrAdmin() {
        return ResponseEntity.ok("Acceso concedido: eres ADMIN_SAAS u ODONTOLOGO");
    }
}
