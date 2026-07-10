package com.backend_sistema_clinico.n8n.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class TurnoN8NResponse {

    private Long turnoId;
    private String initPoint;
    private String estado;

}
