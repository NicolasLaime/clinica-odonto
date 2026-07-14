package com.backend_sistema_clinico.mensaje.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EnviarMensajeRequest {
    private String contenido;
    private String tipo;
}
