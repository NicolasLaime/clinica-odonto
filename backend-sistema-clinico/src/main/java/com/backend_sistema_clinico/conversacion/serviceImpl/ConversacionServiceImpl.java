package com.backend_sistema_clinico.conversacion.serviceImpl;
import com.backend_sistema_clinico.conversacion.dto.ConversacionDto;
import com.backend_sistema_clinico.conversacion.dto.UpdateConversacionRequest;
import com.backend_sistema_clinico.conversacion.entity.Conversacion;
import com.backend_sistema_clinico.conversacion.repository.ConversacionRepository;
import com.backend_sistema_clinico.conversacion.service.ConversacionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConversacionServiceImpl implements ConversacionService {

    private final ConversacionRepository repository;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ConversacionDto obtener(String telefono, String clinicaId) {
        return repository.findByTelefonoAndClinicaId(telefono, clinicaId)
                .map(c -> new ConversacionDto(c.getTelefono(), c.getEstado(), c.getContexto(), c.getClinicaId()))
                .orElse(new ConversacionDto(telefono, "INICIO", null, clinicaId));
    }

    @Override
    public ConversacionDto actualizar(String telefono, UpdateConversacionRequest request, String clinicaId) {
        Conversacion c = repository.findByTelefonoAndClinicaId(telefono, clinicaId)
                .orElseGet(() -> Conversacion.builder()
                        .telefono(telefono)
                        .clinicaId(clinicaId)
                        .build()
                );
        if (request.getEstado() != null) c.setEstado(request.getEstado());
        if (request.getContexto() != null) {
            try {
                String contextoStr;
                Object ctx = request.getContexto();
                if (ctx instanceof String s) {
                    contextoStr = objectMapper.writeValueAsString(
                            objectMapper.readValue(s, new TypeReference<Map<String, Object>>() {}));
                } else {
                    contextoStr = objectMapper.writeValueAsString(ctx);
                }
                c.setContexto(contextoStr);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error al serializar contexto", e);
            }
        }
        repository.save(c);
        return new ConversacionDto(c.getTelefono(), c.getEstado(), c.getContexto(), c.getClinicaId());
    }

    @Override
    public void eliminar(String telefono, String clinicaId) {
        repository.findByTelefonoAndClinicaId(telefono, clinicaId)
                .ifPresent(repository::delete);
    }

    @Override
    public List<ConversacionDto> listar(String clinicaId) {
        return repository.findByClinicaId(clinicaId)
                .stream()
                .map(c -> new ConversacionDto(c.getTelefono(), c.getEstado(), c.getContexto(), c.getClinicaId()))
                .toList();
    }

}
