package com.backend_sistema_clinico.clinica.serviceimpl;

import com.backend_sistema_clinico.clinica.dto.ClinicaDto;
import com.backend_sistema_clinico.clinica.dto.CreateClinicaRequest;
import com.backend_sistema_clinico.clinica.entity.Clinica;
import com.backend_sistema_clinico.clinica.repository.ClinicaRepository;
import com.backend_sistema_clinico.clinica.service.ClinicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClinicaServiceImpl implements ClinicaService {
    private final ClinicaRepository repository;

    @Override
    public ClinicaDto crear(CreateClinicaRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe una clínica con ese email");
        }

        Clinica clinica = Clinica.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .build();

        repository.save(clinica);

        return toDTO(clinica);
    }

    @Override
    public ClinicaDto buscarPorApiKey(String apiKey) {
        Clinica clinica = repository.findByApiKey(apiKey)
                .orElseThrow(() -> new RuntimeException("API Key inválida"));
        if (!clinica.isActivo()) {
            throw new RuntimeException("Clínica desactivada");
        }
        return toDTO(clinica);
    }

    private ClinicaDto toDTO(Clinica c) {
        return new ClinicaDto(c.getId(), c.getNombre(), c.getEmail(), c.getApiKey(), c.isActivo());
    }


}
