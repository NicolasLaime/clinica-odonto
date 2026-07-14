package com.backend_sistema_clinico.mensaje.entity;

import com.backend_sistema_clinico.mensaje.enums.MensajeDireccion;
import com.backend_sistema_clinico.mensaje.enums.MensajeTipo;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clinicaId;

    @Column(nullable = false)
    private String telefono;

    private String nombre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MensajeTipo tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MensajeDireccion direccion;

    private String waMessageId;

    @Builder.Default
    private boolean leido = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoAt;

    @PrePersist
    protected void onCreate() {
        creadoAt = LocalDateTime.now();
    }
}
