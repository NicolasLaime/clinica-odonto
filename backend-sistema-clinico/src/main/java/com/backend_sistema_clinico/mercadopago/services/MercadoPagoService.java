package com.backend_sistema_clinico.mercadopago.services;

import com.backend_sistema_clinico.mercadopago.dto.CreatePreferenceRequest;
import com.backend_sistema_clinico.mercadopago.dto.CreatePreferenceResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class MercadoPagoService {

    @Value("${app.mp.notification-url}")
    private String notificationUrl;

    public CreatePreferenceResponse crearPreferencia(CreatePreferenceRequest req) {
        try {
            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .title(req.getTitulo())
                    .quantity(1)
                    .unitPrice(BigDecimal.valueOf(req.getMonto()))
                    .build();

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("http://localhost:5173/turnos/" + req.getTurnoId() + "/exito")
                    .pending("http://localhost:5173/turnos/" + req.getTurnoId() + "/pendiente")
                    .failure("http://localhost:5173/turnos/" + req.getTurnoId() + "/error")
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(List.of(item))
                    .backUrls(backUrls)
                    .notificationUrl(notificationUrl)
                    .externalReference(req.getTurnoId().toString())
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            return new CreatePreferenceResponse(
                    preference.getInitPoint(),
                    preference.getId()
            );
        } catch (MPApiException e) {
            System.out.println("Status: " + e.getApiResponse().getStatusCode());
            System.out.println("Body: " + e.getApiResponse().getContent());
            throw new RuntimeException("Error al crear preferencia MP: " + e.getMessage(), e);
        } catch (MPException e) {
            throw new RuntimeException("Error al crear preferencia MP: " + e.getMessage(), e);
        }
    }







}
