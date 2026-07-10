package com.backend_sistema_clinico.conversacion.service;

import com.backend_sistema_clinico.conversacion.dto.ConversacionDto;
import com.backend_sistema_clinico.conversacion.dto.UpdateConversacionRequest;

import java.util.List;


public interface ConversacionService {
    ConversacionDto obtener(String telefono, String clinicaId);
    ConversacionDto actualizar(String telefono, UpdateConversacionRequest request, String clinicaId);
    void eliminar(String telefono, String clinicaId);
    List<ConversacionDto> listar(String clinicaId);

}
