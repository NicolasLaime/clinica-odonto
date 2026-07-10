package com.backend_sistema_clinico.turno.dto;

import com.backend_sistema_clinico.especialidad.dto.EspecialidadDTO;
import com.backend_sistema_clinico.paciente.dto.PacienteDTO;
import com.backend_sistema_clinico.turno.entity.EstadoTurno;
import com.backend_sistema_clinico.turno.entity.TipoTurno;
import com.backend_sistema_clinico.user.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class TurnoDTO {
    private Long id;
    private PacienteDTO paciente;
    private UserDTO odontologo;
    private EspecialidadDTO especialidad;
    private LocalDateTime fechaHora;
    private Integer duracionMinutos;
    private TipoTurno tipo;
    private EstadoTurno estado;
    private String diagnostico;
    private Double precio;
    private Double precioTotal;
    private Double senia;
    private String clinicaId;
    private Double pagoEnConsultorio;
    private String callbackUrl;
    private String linkPago;
    private String initPoint;
    private List<TurnoImagenDTO> imagenes;

}
