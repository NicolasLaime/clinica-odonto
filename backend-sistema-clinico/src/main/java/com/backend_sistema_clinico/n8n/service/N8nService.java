package com.backend_sistema_clinico.n8n.service;

import com.backend_sistema_clinico.mercadopago.dto.CreatePreferenceRequest;
import com.backend_sistema_clinico.mercadopago.dto.CreatePreferenceResponse;
import com.backend_sistema_clinico.mercadopago.services.MercadoPagoService;
import com.backend_sistema_clinico.n8n.dto.CreateTurnoN8nRequest;
import com.backend_sistema_clinico.n8n.dto.TurnoN8NResponse;
import com.backend_sistema_clinico.paciente.entity.Paciente;
import com.backend_sistema_clinico.paciente.repository.PacienteRepository;
import com.backend_sistema_clinico.turno.entity.EstadoTurno;
import com.backend_sistema_clinico.turno.entity.TipoTurno;
import com.backend_sistema_clinico.turno.entity.Turno;
import com.backend_sistema_clinico.turno.repository.TurnoRepository;
import com.backend_sistema_clinico.user.entity.Usuario;
import com.backend_sistema_clinico.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class N8nService {

    @Value("${app.precio-consulta}")
    private Double precioConsulta;

    @Value("${app.senia}")
    private Double senia;

    private final PacienteRepository pacienteRepository;
    private final UserRepository userRepository;
    private final TurnoRepository turnoRepository;
    private final MercadoPagoService mercadoPagoService;
    private final RestTemplate restTemplate;

    public TurnoN8NResponse crearTurno(CreateTurnoN8nRequest request) {
        Paciente paciente = pacienteRepository.findByTelefono(request.getTelefono())
                .orElseGet(() -> pacienteRepository.save(
                        Paciente.builder()
                                .nombre(request.getNombre())
                                .apellido(request.getApellido() != null ? request.getApellido() : "")
                                .telefono(request.getTelefono())
                                .clinicaId(request.getClinicaId())
                                .activo(true)
                                .build()
                ));

        Usuario odontologo = userRepository.findById(request.getOdontologoId())
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        boolean ocupado = turnoRepository
                .findByOdontologoIdAndFechaHoraBetweenOrderByFechaHoraAsc(
                        request.getOdontologoId(),
                        request.getFechaHora().toLocalDate().atStartOfDay(),
                        request.getFechaHora().toLocalDate().atTime(LocalTime.MAX))
                .stream()
                .anyMatch(t -> t.getEstado() != EstadoTurno.CANCELADO &&
                        request.getFechaHora().isBefore(
                                t.getFechaHora().plusMinutes(t.getDuracionMinutos() != null ? t.getDuracionMinutos() : 60)) &&
                        request.getFechaHora().plusMinutes(60).isAfter(t.getFechaHora()));

        if (ocupado) {
            throw new RuntimeException("El odontólogo ya tiene un turno en ese horario");
        }

        Turno turno = Turno.builder()
                .paciente(paciente)
                .odontologo(odontologo)
                .fechaHora(request.getFechaHora())
                .duracionMinutos(60)
                .tipo(TipoTurno.CONSULTA)
                .precioTotal(precioConsulta)
                .precio(precioConsulta)
                .senia(senia)
                .clinicaId(request.getClinicaId())
                .estado(EstadoTurno.PENDIENTE_PAGO)
                .callbackUrl(request.getCallbackUrl())
                .build();

        turnoRepository.save(turno);

        CreatePreferenceResponse mp = mercadoPagoService.crearPreferencia(
                new CreatePreferenceRequest(
                        "Consulta - " + turno.getFechaHora().toLocalDate(),
                        turno.getSenia(),
                        turno.getId()
                )
        );

        turno.setInitPoint(mp.getInitPoint());
        turno.setPreferenceId(mp.getPreferenceId());
        turnoRepository.save(turno);

        return new TurnoN8NResponse(turno.getId(), mp.getInitPoint(), "PENDIENTE_PAGO");
    }

}
