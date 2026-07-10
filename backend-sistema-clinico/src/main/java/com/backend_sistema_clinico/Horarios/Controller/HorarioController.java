package com.backend_sistema_clinico.Horarios.Controller;

import com.backend_sistema_clinico.Horarios.Dto.CreateHorarioRequest;
import com.backend_sistema_clinico.Horarios.Dto.HorarioDTO;
import com.backend_sistema_clinico.Horarios.service.HorarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/api/v1/horarios")
@RequiredArgsConstructor
public class HorarioController {

    private final HorarioService service;

    @PostMapping
    public ResponseEntity<HorarioDTO> create(@Valid @RequestBody CreateHorarioRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping("/odontologo/{odontologoId}")
    public ResponseEntity<List<HorarioDTO>> findByOdontologo(@PathVariable Long odontologoId) {
        return ResponseEntity.ok(service.findByOdontologo(odontologoId));
    }

    @GetMapping("/dia/{dia}")
    public ResponseEntity<List<HorarioDTO>> findByDia(@PathVariable DayOfWeek dia) {
        return ResponseEntity.ok(service.findByDia(dia));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
