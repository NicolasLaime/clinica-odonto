package com.backend_sistema_clinico.mercadopago.services;


import com.backend_sistema_clinico.turno.service.TurnoService;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.backend_sistema_clinico.turno.dto.TurnoDTO;
import org.springframework.web.client.RestTemplate;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class WebhookService {

    private final TurnoService turnoService;
    private final RestTemplate restTemplate;


    public void procesarPagoAprobado(String paymentId) {
        try {
            PaymentClient paymentClient = new PaymentClient();
            Payment payment = paymentClient.get(Long.parseLong(paymentId));

            if ("approved".equals(payment.getStatus())) {
                String turnoId = payment.getExternalReference();
                TurnoDTO turno = turnoService.confirmarPago(Long.parseLong(turnoId));

                if (turno.getCallbackUrl() != null && !turno.getCallbackUrl().isEmpty()) {
                    restTemplate.postForEntity(
                            turno.getCallbackUrl(),
                            Map.of(
                                    "turnoId", Long.parseLong(turnoId),
                                    "estado", "CONFIRMADO",
                                    "telefono", turno.getPaciente().getTelefono()
                            ),
                            String.class
                    );

                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar pago MP: " + e.getMessage(), e);
        }
    }

}
