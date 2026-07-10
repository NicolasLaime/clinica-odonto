package com.backend_sistema_clinico.Horarios.repository;

import com.backend_sistema_clinico.Horarios.entity.Horarios;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface HorarioRepository extends JpaRepository<Horarios,Long> {

    List<Horarios> findByOdontologoIdAndActivoTrue(Long odontologoId);
    List<Horarios> findByDiaSemanaAndActivoTrue(DayOfWeek diaSemana);
    List<Horarios> findByClinicaId(String clinicaId);




}
