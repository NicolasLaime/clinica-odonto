package com.backend_sistema_clinico.conversacion.repository;

import com.backend_sistema_clinico.conversacion.entity.Conversacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ConversacionRepository extends JpaRepository<Conversacion, Long> {

    Optional<Conversacion> findByTelefono(String telefono);
    List<Conversacion> findByClinicaId(String clinicaId);
    Optional<Conversacion> findByTelefonoAndClinicaId(String telefono, String clinicaId);

}
