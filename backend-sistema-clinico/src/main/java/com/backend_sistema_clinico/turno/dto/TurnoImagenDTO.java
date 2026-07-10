package com.backend_sistema_clinico.turno.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class TurnoImagenDTO {

    private Long id;
    private String nombreArchivo;
    private String url;
    private String tipoContenido;

}
