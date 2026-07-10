package com.backend_sistema_clinico.turno.repository;

import com.backend_sistema_clinico.turno.entity.TurnoImagen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TurnoImagenRepository extends JpaRepository<TurnoImagen,Long> {

    List<TurnoImagen> findByTurnoId(Long turnoId);


}
