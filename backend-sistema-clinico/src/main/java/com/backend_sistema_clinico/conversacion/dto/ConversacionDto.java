package com.backend_sistema_clinico.conversacion.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversacionDto {

    private String telefono;
    private String estado;
    private String contexto;
    private String clinicaId;

}
