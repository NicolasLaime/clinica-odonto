package com.backend_sistema_clinico.turno.repository;

import com.backend_sistema_clinico.turno.entity.EstadoTurno;
import com.backend_sistema_clinico.turno.entity.Turno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    List<Turno> findByOdontologoIdAndFechaHoraBetweenOrderByFechaHoraAsc(
            Long odontologoId, LocalDateTime desde, LocalDateTime hasta);

    List<Turno> findByFechaHoraBetweenAndEstado(
            LocalDateTime desde, LocalDateTime hasta, EstadoTurno estado);

    List<Turno> findByPacienteIdOrderByFechaHoraDesc(Long pacienteId);

    List<Turno> findByEstadoAndCreatedAtBefore(EstadoTurno estado, LocalDateTime fecha);

    List<Turno> findByClinicaId(String clinicaId);

    Page<Turno> findAllByClinicaId(String clinicaId, Pageable pageable);

    Optional<Turno> findByIdAndClinicaId(Long id, String clinicaId);

    List<Turno> findByPacienteIdAndClinicaIdOrderByFechaHoraDesc(Long pacienteId, String clinicaId);


// ===== Dashboard queries =====

    long countByClinicaIdAndEstadoAndFechaHoraBetween(
            String clinicaId, EstadoTurno estado, LocalDateTime desde, LocalDateTime hasta);

    @Query("SELECT COALESCE(SUM(t.senia), 0) FROM Turno t WHERE t.clinicaId = :clinicaId AND t.estado IN :estados AND t.fechaHora BETWEEN :desde AND :hasta")
    Double sumSeniaPorClinica(@Param("clinicaId") String clinicaId, @Param("estados") List<EstadoTurno> estados, @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("SELECT COALESCE(SUM(t.pagoEnConsultorio), 0) FROM Turno t WHERE t.clinicaId = :clinicaId AND t.estado = 'COMPLETADO' AND t.fechaHora BETWEEN :desde AND :hasta")
    Double sumPagoConsultorioPorClinica(@Param("clinicaId") String clinicaId, @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("SELECT COUNT(DISTINCT t.paciente.id) FROM Turno t WHERE t.clinicaId = :clinicaId AND t.estado = 'COMPLETADO' AND t.fechaHora BETWEEN :desde AND :hasta")
    Long countPacientesDistintos(@Param("clinicaId") String clinicaId, @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);



}
