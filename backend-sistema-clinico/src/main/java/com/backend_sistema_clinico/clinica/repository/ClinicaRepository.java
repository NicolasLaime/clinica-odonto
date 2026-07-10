package com.backend_sistema_clinico.clinica.repository;
import com.backend_sistema_clinico.clinica.entity.Clinica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClinicaRepository extends JpaRepository<Clinica, String> {

    Optional<Clinica> findByApiKey(String apiKey);
    boolean existsByEmail(String email);


}
