package com.backend_sistema_clinico.conversacion.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConversacionRequest {

    private String estado;
    private Object contexto;
    private String clinicaId;
}
