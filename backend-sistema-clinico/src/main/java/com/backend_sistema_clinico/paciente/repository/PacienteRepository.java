package com.backend_sistema_clinico.paciente.repository;

import com.backend_sistema_clinico.paciente.entity.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface PacienteRepository extends JpaRepository<Paciente,Long> {

    boolean existsByEmail(String email);
    List<Paciente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);
    List<Paciente> findByDni(String dni);
    Optional<Paciente> findByTelefono(String telefono);
    List<Paciente> findByClinicaId(String clinicaId);
    Page<Paciente> findAllByClinicaId(String clinicaId, Pageable pageable);
    Optional<Paciente> findByIdAndClinicaId(Long id, String clinicaId);
    List<Paciente> findByClinicaIdAndNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
            String clinicaId, String nombre, String apellido);
    Optional<Paciente> findByDniAndClinicaId(String dni, String clinicaId);

}
