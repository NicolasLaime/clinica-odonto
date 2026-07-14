package com.backend_sistema_clinico.mensaje.serviceimpl;

import com.backend_sistema_clinico.mensaje.dto.*;
import com.backend_sistema_clinico.mensaje.entity.Mensaje;
import com.backend_sistema_clinico.mensaje.enums.MensajeDireccion;
import com.backend_sistema_clinico.mensaje.enums.MensajeTipo;
import com.backend_sistema_clinico.mensaje.repository.MensajeRepository;
import com.backend_sistema_clinico.mensaje.service.MensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MensajeServiceImpl implements MensajeService {

    private final MensajeRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<MensajeDTO> listar(String clinicaId) {
        return repository.findByClinicaIdOrderByCreadoAtDesc(clinicaId)
                .stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MensajeDTO> listarPorTelefono(String clinicaId, String telefono) {
        return repository.findByClinicaIdAndTelefonoOrderByCreadoAtAsc(clinicaId, telefono)
                .stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public MensajeDTO guardar(String clinicaId, GuardarMensajeRequest request) {
        var mensaje = Mensaje.builder()
                .clinicaId(clinicaId)
                .telefono(request.getTelefono())
                .nombre(request.getNombre())
                .contenido(request.getContenido())
                .tipo(MensajeTipo.valueOf(request.getTipo()))
                .direccion(MensajeDireccion.valueOf(request.getDireccion()))
                .waMessageId(request.getWaMessageId())
                .build();
        return toDTO(repository.save(mensaje));
    }

    @Override
    @Transactional
    public MensajeDTO enviar(String clinicaId, String telefono, EnviarMensajeRequest request) {
        var mensaje = Mensaje.builder()
                .clinicaId(clinicaId)
                .telefono(telefono)
                .contenido(request.getContenido())
                .tipo(MensajeTipo.valueOf(request.getTipo()))
                .direccion(MensajeDireccion.SALIENTE)
                .build();
        return toDTO(repository.save(mensaje));
    }

    @Override
    @Transactional
    public void marcarComoLeidos(String clinicaId, String telefono) {
        repository.marcarComoLeidos(clinicaId, telefono);
    }

    private MensajeDTO toDTO(Mensaje m) {
        return MensajeDTO.builder()
                .id(m.getId())
                .clinicaId(m.getClinicaId())
                .telefono(m.getTelefono())
                .nombre(m.getNombre())
                .contenido(m.getContenido())
                .tipo(m.getTipo().name())
                .direccion(m.getDireccion().name())
                .waMessageId(m.getWaMessageId())
                .leido(m.isLeido())
                .creadoAt(m.getCreadoAt())
                .build();
    }
}
