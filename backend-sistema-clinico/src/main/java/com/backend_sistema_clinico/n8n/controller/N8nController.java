package com.backend_sistema_clinico.n8n.controller;

import com.backend_sistema_clinico.n8n.dto.CreateTurnoN8nRequest;
import com.backend_sistema_clinico.n8n.dto.TurnoN8NResponse;
import com.backend_sistema_clinico.n8n.service.N8nService;
import com.backend_sistema_clinico.paciente.dto.CreatePacienteDTO;
import com.backend_sistema_clinico.paciente.dto.PacienteDTO;
import com.backend_sistema_clinico.turno.dto.SlotDTO;
import com.backend_sistema_clinico.turno.service.TurnoService;
import com.backend_sistema_clinico.user.dto.UserDTO;
import com.backend_sistema_clinico.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/n8n")
@RequiredArgsConstructor
public class N8nController {

    private final N8nService n8nService;
    private final TurnoService turnoService;
    private final UserService userService;

    @PostMapping("/turnos")
    public ResponseEntity<TurnoN8NResponse> crearTurno(
            @RequestBody CreateTurnoN8nRequest request,
            HttpServletRequest httpRequest) {
        String clinicaId = (String) httpRequest.getAttribute("clinicaId");
        request.setClinicaId(clinicaId);
        return ResponseEntity.ok(n8nService.crearTurno(request));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<SlotDTO>> disponibles(
            @RequestParam LocalDate fecha,
            @RequestParam Long odontologoId) {
        return ResponseEntity.ok(turnoService.findDisponibles(fecha, odontologoId));
    }

    @GetMapping("/odontologos")
    public ResponseEntity<List<UserDTO>> listarOdontologos(HttpServletRequest request) {
        String clinicaId = (String) request.getAttribute("clinicaId");
        return ResponseEntity.ok(userService.findOdontologosByClinicaId(clinicaId));
    }

    @GetMapping("/pacientes/buscar")
    public ResponseEntity<?> buscarPorDni(@RequestParam String dni, HttpServletRequest request) {
        String clinicaId = (String) request.getAttribute("clinicaId");
        PacienteDTO paciente = n8nService.buscarPorDni(dni, clinicaId);
        if (paciente == null) {
            return ResponseEntity.ok(java.util.Map.of("found", false, "message", "Paciente no encontrado"));
        }
        return ResponseEntity.ok(paciente);
    }

    @PostMapping("/pacientes")
    public ResponseEntity<PacienteDTO> crearPaciente(
            @Valid @RequestBody CreatePacienteDTO request,
            HttpServletRequest httpRequest) {
        String clinicaId = (String) httpRequest.getAttribute("clinicaId");
        return ResponseEntity.ok(n8nService.crearPaciente(request, clinicaId));
    }

    @GetMapping("/turnos/{id}")
    public ResponseEntity<TurnoN8NResponse> estadoTurno(@PathVariable Long id) {
        var turno = turnoService.findById(id);
        return ResponseEntity.ok(new TurnoN8NResponse(
                turno.getId(),
                turno.getInitPoint(),
                turno.getEstado().name()
        ));
    }


}
