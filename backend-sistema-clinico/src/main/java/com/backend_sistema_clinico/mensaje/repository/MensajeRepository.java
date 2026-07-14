package com.backend_sistema_clinico.mensaje.repository;

import com.backend_sistema_clinico.mensaje.entity.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    List<Mensaje> findByClinicaIdOrderByCreadoAtDesc(String clinicaId);

    List<Mensaje> findByClinicaIdAndTelefonoOrderByCreadoAtAsc(String clinicaId, String telefono);

    List<Mensaje> findByClinicaIdAndTelefonoOrderByCreadoAtDesc(String clinicaId, String telefono);

    @Modifying
    @Query("UPDATE Mensaje m SET m.leido = true WHERE m.clinicaId = ?1 AND m.telefono = ?2 AND m.direccion = 'ENTRANTE'")
    void marcarComoLeidos(String clinicaId, String telefono);
}
