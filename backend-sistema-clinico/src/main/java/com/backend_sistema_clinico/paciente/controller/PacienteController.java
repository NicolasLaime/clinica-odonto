package com.backend_sistema_clinico.paciente.controller;

import com.backend_sistema_clinico.paciente.dto.CreatePacienteDTO;
import com.backend_sistema_clinico.paciente.dto.PacienteDTO;
import com.backend_sistema_clinico.paciente.service.PacienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pacientes")
@RequiredArgsConstructor
public class PacienteController {


    private final PacienteService service;

    @GetMapping
    public ResponseEntity<Page<PacienteDTO>> findAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<PacienteDTO> create(@Valid @RequestBody CreatePacienteDTO request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteDTO> update(@PathVariable Long id, @Valid @RequestBody CreatePacienteDTO request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<PacienteDTO>> buscar(@RequestParam String q) {
        return ResponseEntity.ok(service.buscar(q));
    }





}
