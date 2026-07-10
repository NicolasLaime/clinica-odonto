package com.backend_sistema_clinico.clinica.controller;
import com.backend_sistema_clinico.clinica.dto.ClinicaDto;
import com.backend_sistema_clinico.clinica.dto.CreateClinicaRequest;
import com.backend_sistema_clinico.clinica.service.ClinicaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clinicas")
@RequiredArgsConstructor
public class ClinicaController {

    private final ClinicaService service;

    @PostMapping
    public ResponseEntity<ClinicaDto> crearClinica(@Valid @RequestBody CreateClinicaRequest request) {
        return ResponseEntity.ok(service.crear(request));
    }


}
