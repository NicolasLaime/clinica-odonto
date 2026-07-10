package com.backend_sistema_clinico.clinica.service;

import com.backend_sistema_clinico.clinica.dto.ClinicaDto;
import com.backend_sistema_clinico.clinica.dto.CreateClinicaRequest;

public interface ClinicaService {

    ClinicaDto crear(CreateClinicaRequest request);
    ClinicaDto buscarPorApiKey(String apiKey);


}
