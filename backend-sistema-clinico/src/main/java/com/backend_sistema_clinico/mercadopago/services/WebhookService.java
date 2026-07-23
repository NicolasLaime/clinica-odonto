package com.backend_sistema_clinico.mercadopago.services;

import com.backend_sistema_clinico.conversacion.dto.UpdateConversacionRequest;
import com.backend_sistema_clinico.conversacion.service.ConversacionService;
import com.backend_sistema_clinico.turno.service.TurnoService;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import com.backend_sistema_clinico.turno.dto.TurnoDTO;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class WebhookService {

    private final TurnoService turnoService;
    private final RestTemplate restTemplate;
    private final ConversacionService conversacionService;

    @Value("${app.n8n.webhook-confirmacion}")
    private String n8nWebhookConfirmacion;


    public void procesarPagoAprobado(String paymentId) {
        try {
            System.out.println("=== PROCESANDO PAGO ===");
            System.out.println("PaymentId: " + paymentId);
            PaymentClient paymentClient = new PaymentClient();
            Payment payment = paymentClient.get(Long.parseLong(paymentId));
            System.out.println("=== PAGO MP OBTENIDO ===");
            System.out.println("Status: " + payment.getStatus());
            System.out.println("ExternalReference: " + payment.getExternalReference());

            if ("approved".equals(payment.getStatus())) {
                String turnoId = payment.getExternalReference();
                System.out.println("TurnoId del externalReference: " + turnoId);
                TurnoDTO turno = turnoService.confirmarPagoSinAuth(Long.parseLong(turnoId));
                System.out.println("=== TURNO CONFIRMADO ===");
                System.out.println("Turno ID: " + turno.getId());

                UpdateConversacionRequest updateConversacion = new UpdateConversacionRequest();
                updateConversacion.setEstado("TURNO_CONFIRMADO");
                conversacionService.actualizar(turno.getPaciente().getTelefono(), updateConversacion, turno.getClinicaId());
                System.out.println("=== CONVERSACION ACTUALIZADA ===");

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(Map.of(
                        "turnoId", turno.getId(),
                        "telefono", turno.getPaciente().getTelefono(),
                        "clinicaId", turno.getClinicaId(),
                        "odontologoNombre", turno.getOdontologo().getFirstName() + " " + turno.getOdontologo().getLastName(),
                        "fecha", turno.getFechaHora().toLocalDate().toString(),
                        "hora", turno.getFechaHora().toLocalTime().toString()
                ), headers);

                try {
                    restTemplate.postForEntity(n8nWebhookConfirmacion, request, String.class);
                    System.out.println("=== N8N WEBHOOK DE CONFIRMACION ENVIADO ===");
                } catch (Exception e) {
                    System.out.println("=== ERROR AL ENVIAR WEBHOOK N8N ===");
                    e.printStackTrace();
                }
            } else {
                System.out.println("=== PAGO NO APROBADO ===");
                System.out.println("Status: " + payment.getStatus());
            }
        } catch (Exception e) {
            System.out.println("=== ERROR AL PROCESAR PAGO MP ===");
            e.printStackTrace();
            throw new RuntimeException("Error al procesar pago MP: " + e.getMessage(), e);
        }
    }

}
