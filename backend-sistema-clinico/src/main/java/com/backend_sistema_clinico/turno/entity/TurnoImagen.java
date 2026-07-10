package com.backend_sistema_clinico.turno.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "turno_imagenes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TurnoImagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_id", nullable = false)
    private Turno turno;

    @Column(nullable = false)
    private String nombreArchivo;

    @Column(nullable = false)
    private String rutaArchivo;

    @Column(nullable = false)
    private String tipoContenido;
}
