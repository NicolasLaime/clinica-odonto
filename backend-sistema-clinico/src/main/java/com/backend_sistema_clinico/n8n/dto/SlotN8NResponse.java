package com.backend_sistema_clinico.n8n.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class SlotN8NResponse {

    private LocalDateTime fechaHora;
    private Integer duracionMinutos;


}
