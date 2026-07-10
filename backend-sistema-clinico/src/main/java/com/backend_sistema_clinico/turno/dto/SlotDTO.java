package com.backend_sistema_clinico.turno.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotDTO {
    private LocalDateTime fechaHora;
    private Integer duracionMinutos;
}
