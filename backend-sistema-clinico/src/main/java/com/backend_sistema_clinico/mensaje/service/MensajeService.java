package com.backend_sistema_clinico.mensaje.service;

import com.backend_sistema_clinico.mensaje.dto.*;
import java.util.List;

public interface MensajeService {

    List<MensajeDTO> listar(String clinicaId);

    List<MensajeDTO> listarPorTelefono(String clinicaId, String telefono);

    MensajeDTO guardar(String clinicaId, GuardarMensajeRequest request);

    MensajeDTO enviar(String clinicaId, String telefono, EnviarMensajeRequest request);

    void marcarComoLeidos(String clinicaId, String telefono);
}
