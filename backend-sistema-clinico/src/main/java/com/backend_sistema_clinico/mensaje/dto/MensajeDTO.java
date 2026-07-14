package com.backend_sistema_clinico.mensaje.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MensajeDTO {
    private Long id;
    private String clinicaId;
    private String telefono;
    private String nombre;
    private String contenido;
    private String tipo;
    private String direccion;
    private String waMessageId;
    private boolean leido;
    private LocalDateTime creadoAt;
}
