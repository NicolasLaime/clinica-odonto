package com.backend_sistema_clinico.reportes.controller;

import com.backend_sistema_clinico.reportes.dto.ReportesDto;
import com.backend_sistema_clinico.reportes.service.ReporteService;
import com.backend_sistema_clinico.shared.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReportesController {

    private final ReporteService reporteService;

    @GetMapping("/resumen")
    public ResponseEntity<ReportesDto> resumen() {
        return ResponseEntity.ok(reporteService.resumen(SecurityUtils.obtenerClinicaId()));
    }


}
