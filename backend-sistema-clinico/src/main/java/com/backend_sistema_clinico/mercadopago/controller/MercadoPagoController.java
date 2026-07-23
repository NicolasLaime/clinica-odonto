package com.backend_sistema_clinico.mercadopago.controller;


import com.backend_sistema_clinico.mercadopago.dto.WebhookNotification;
import com.backend_sistema_clinico.mercadopago.services.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mercadopago")
@RequiredArgsConstructor
public class MercadoPagoController {

    private final WebhookService webhookService;

    @PostMapping("/webhook")
    public ResponseEntity<String> recibirNotificacion(@RequestBody WebhookNotification notificacion) {
        System.out.println("=== WEBHOOK MERCADOPAGO RECIBIDO ===");
        System.out.println("Type: " + notificacion.getType());
        System.out.println("Data: " + notificacion.getData());
        if ("payment".equals(notificacion.getType()) && notificacion.getData() != null) {
            String paymentId = String.valueOf(notificacion.getData().get("id"));
            webhookService.procesarPagoAprobado(paymentId);
        }
        return ResponseEntity.ok("OK");
    }



}
