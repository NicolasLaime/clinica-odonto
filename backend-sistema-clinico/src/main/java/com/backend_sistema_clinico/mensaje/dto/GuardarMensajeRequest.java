package com.backend_sistema_clinico.mensaje.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class GuardarMensajeRequest {
    private String telefono;
    private String nombre;
    private String contenido;
    private String tipo;
    private String direccion;
    private String waMessageId;
}
