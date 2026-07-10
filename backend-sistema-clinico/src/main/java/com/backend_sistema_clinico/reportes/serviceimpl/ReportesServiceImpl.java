package com.backend_sistema_clinico.reportes.serviceimpl;

import com.backend_sistema_clinico.reportes.dto.ReportesDto;
import com.backend_sistema_clinico.reportes.service.ReporteService;
import com.backend_sistema_clinico.turno.entity.EstadoTurno;
import com.backend_sistema_clinico.turno.repository.TurnoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportesServiceImpl implements ReporteService {

    private final TurnoRepository turnoRepository;

    @Override
    public ReportesDto resumen(String clinicaId) {

        LocalDate hoy = LocalDate.now();

        LocalDateTime inicioHoy = hoy.atStartOfDay();

        LocalDateTime finHoy = hoy.atTime(LocalTime.MAX);


        LocalDate lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        LocalDateTime inicioSemana = lunes.atStartOfDay();

        LocalDateTime finSemana = lunes.plusDays(6).atTime(LocalTime.MAX);

        LocalDate inicioMes = hoy.withDayOfMonth(1);

        LocalDateTime inicioMesDT = inicioMes.atStartOfDay();

        LocalDateTime finMesDT = hoy.withDayOfMonth(hoy.lengthOfMonth()).atTime(LocalTime.MAX);

        List<EstadoTurno> estadosPago = List.of(EstadoTurno.CONFIRMADO, EstadoTurno.COMPLETADO);

        long turnosHoy = turnoRepository.countByClinicaIdAndEstadoAndFechaHoraBetween(clinicaId, EstadoTurno.COMPLETADO, inicioHoy, finHoy);

        long turnosSemana = turnoRepository.countByClinicaIdAndEstadoAndFechaHoraBetween(clinicaId, EstadoTurno.COMPLETADO, inicioSemana, finSemana);

        long turnosMes = turnoRepository.countByClinicaIdAndEstadoAndFechaHoraBetween(clinicaId, EstadoTurno.COMPLETADO, inicioMesDT, finMesDT);

        double seniasHoy = turnoRepository.sumSeniaPorClinica(clinicaId, estadosPago, inicioHoy, finHoy);

        double seniasSemana = turnoRepository.sumSeniaPorClinica(clinicaId, estadosPago, inicioSemana, finSemana);

        double seniasMes = turnoRepository.sumSeniaPorClinica(clinicaId, estadosPago, inicioMesDT, finMesDT);

        double consultorioHoy = turnoRepository.sumPagoConsultorioPorClinica(clinicaId, inicioHoy, finHoy);

        double consultorioSemana = turnoRepository.sumPagoConsultorioPorClinica(clinicaId, inicioSemana, finSemana);

        double consultorioMes = turnoRepository.sumPagoConsultorioPorClinica(clinicaId, inicioMesDT, finMesDT);

        long pacientes = turnoRepository.countPacientesDistintos(clinicaId, inicioMesDT, finMesDT);

        long cancelados = turnoRepository.countByClinicaIdAndEstadoAndFechaHoraBetween(clinicaId, EstadoTurno.CANCELADO, inicioMesDT, finMesDT);

        long totalMes = turnosMes + cancelados;

        double tasa = totalMes > 0 ? Math.round(cancelados * 10000.0 / totalMes) / 100.0 : 0;

        return new ReportesDto(
                turnosHoy, turnosSemana, turnosMes,
                Math.round((seniasHoy + consultorioHoy) * 100.0) / 100.0,
                Math.round((seniasSemana + consultorioSemana) * 100.0) / 100.0,
                Math.round((seniasMes + consultorioMes) * 100.0) / 100.0,
                Math.round(seniasHoy * 100.0) / 100.0,
                Math.round(seniasSemana * 100.0) / 100.0,
                Math.round(seniasMes * 100.0) / 100.0,
                Math.round(consultorioHoy * 100.0) / 100.0,
                Math.round(consultorioSemana * 100.0) / 100.0,
                Math.round(consultorioMes * 100.0) / 100.0,
                pacientes, cancelados, tasa
        );
    }




}
