package com.backend_sistema_clinico.mercadopago.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class CreatePreferenceRequest {

    private String titulo;
    private Double monto;
    private Long turnoId;

}
