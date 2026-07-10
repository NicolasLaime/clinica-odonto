package com.backend_sistema_clinico.reportes.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportesDto {

        private long turnosHoy;
        private long turnosSemana;
        private long turnosMes;
        private double ingresosHoy;
        private double ingresosSemana;
        private double ingresosMes;
        private double seniasHoy;
        private double seniasSemana;
        private double seniasMes;
        private double consultorioHoy;
        private double consultorioSemana;
        private double consultorioMes;
        private long pacientesAtendidosMes;
        private long canceladosMes;
        private double tasaCancelacion;

}
