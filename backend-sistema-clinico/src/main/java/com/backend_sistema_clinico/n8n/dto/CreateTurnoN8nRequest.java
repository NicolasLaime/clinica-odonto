package com.backend_sistema_clinico.n8n.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

 @Data
 @NoArgsConstructor
 @AllArgsConstructor
 public class CreateTurnoN8nRequest {
        private Long pacienteId;
        private String nombre;
        private String apellido;
        private String telefono;
        private Long odontologoId;
        private Double precioTotal;
        private Double senia;
        private LocalDateTime fechaHora;
        private String callbackUrl;
          private String clinicaId;




 }



