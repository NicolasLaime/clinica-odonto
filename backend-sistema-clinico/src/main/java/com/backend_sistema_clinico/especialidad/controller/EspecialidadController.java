package com.backend_sistema_clinico.especialidad.controller;

import com.backend_sistema_clinico.especialidad.dto.CreateEspecialidadRequest;
import com.backend_sistema_clinico.especialidad.dto.EspecialidadDTO;
import com.backend_sistema_clinico.especialidad.service.EspecialidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/especialidades")
@RequiredArgsConstructor
public class EspecialidadController {


    private final EspecialidadService service;

    @GetMapping
    public ResponseEntity<List<EspecialidadDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<EspecialidadDTO> create(@Valid @RequestBody CreateEspecialidadRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadDTO> update(@PathVariable Long id, @Valid @RequestBody CreateEspecialidadRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }



}
