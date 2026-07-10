package com.backend_sistema_clinico.mercadopago.dto;

import lombok.Data;

import java.util.Map;

@Data
public class WebhookNotification {

    private String action;
    private String type;
    private Map<String, Object> data;


}
