package com.backend_sistema_clinico.conversacion.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversaciones", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"telefono", "clinicaId"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Conversacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private String estado;

    private String clinicaId;

    @Column(columnDefinition = "TEXT")
    private String contexto;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }









}
