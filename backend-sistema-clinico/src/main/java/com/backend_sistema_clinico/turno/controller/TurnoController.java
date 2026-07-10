package com.backend_sistema_clinico.turno.controller;

import com.backend_sistema_clinico.turno.dto.CompletarTurnoRequest;
import com.backend_sistema_clinico.turno.dto.CreateTurnoRequest;
import com.backend_sistema_clinico.turno.dto.SlotDTO;
import com.backend_sistema_clinico.turno.dto.TurnoDTO;
import com.backend_sistema_clinico.turno.service.TurnoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/turnos")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService service;

    @GetMapping
    public ResponseEntity<Page<TurnoDTO>> findAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<SlotDTO>> disponibles(
            @RequestParam LocalDate fecha,
            @RequestParam Long odontologoId) {
        return ResponseEntity.ok(service.findDisponibles(fecha, odontologoId));
    }

    @PostMapping
    public ResponseEntity<TurnoDTO> create(@Valid @RequestBody CreateTurnoRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PostMapping("/{id}/confirmar-pago")
    public ResponseEntity<TurnoDTO> confirmarPago(@PathVariable Long id) {
        return ResponseEntity.ok(service.confirmarPago(id));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<TurnoDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelar(id));
    }

    @PostMapping("/{id}/completar")
    public ResponseEntity<TurnoDTO> completar(
            @PathVariable Long id,
            @RequestBody CompletarTurnoRequest request) {
        return ResponseEntity.ok(service.completar(id, request));
    }
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<TurnoDTO>> findByPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(service.findByPacienteId(pacienteId));
    }

    @PostMapping(value = "/{id}/imagenes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TurnoDTO> subirImagen(@PathVariable Long id, @RequestParam("archivo") MultipartFile archivo) {
        return ResponseEntity.ok(service.subirImagen(id, archivo));
    }

    @GetMapping("/imagenes/{imagenId}")
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable Long imagenId) {
        byte[] img = service.obtenerImagen(imagenId);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(img);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurnoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }


}
