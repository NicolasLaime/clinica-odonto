package com.backend_sistema_clinico.n8n.controller;

import com.backend_sistema_clinico.n8n.dto.CreateTurnoN8nRequest;
import com.backend_sistema_clinico.n8n.dto.TurnoN8NResponse;
import com.backend_sistema_clinico.n8n.service.N8nService;
import com.backend_sistema_clinico.turno.dto.SlotDTO;
import com.backend_sistema_clinico.turno.service.TurnoService;
import jakarta.servlet.http.HttpServletRequest;
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
