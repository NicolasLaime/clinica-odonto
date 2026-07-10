package com.backend_sistema_clinico.turno.serviceImpl;

import com.backend_sistema_clinico.turno.entity.EstadoTurno;
import com.backend_sistema_clinico.turno.repository.TurnoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LiberacionTurnosTask {


    @Value("${app.tiempo-liberacion-minutos}")
    private int minutos;

    private final TurnoRepository turnoRepository;

    @Scheduled(fixedRate = 30000)
    public void liberarTurnosVencidos() {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(minutos);
        var vencidos = turnoRepository.findByEstadoAndCreatedAtBefore(
                EstadoTurno.PENDIENTE_PAGO, limite);

        vencidos.forEach(t -> {
            t.setEstado(EstadoTurno.CANCELADO);
            turnoRepository.save(t);
        });

        if (!vencidos.isEmpty()) {
            System.out.println(vencidos.size() + " turnos liberados por vencimiento");
        }
    }
}
