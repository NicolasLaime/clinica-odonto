package com.backend_sistema_clinico.Horarios.serviceimpl;

import com.backend_sistema_clinico.Horarios.Dto.CreateHorarioRequest;
import com.backend_sistema_clinico.Horarios.Dto.HorarioDTO;
import com.backend_sistema_clinico.Horarios.entity.Horarios;
import com.backend_sistema_clinico.Horarios.repository.HorarioRepository;
import com.backend_sistema_clinico.Horarios.service.HorarioService;
import com.backend_sistema_clinico.user.entity.Usuario;
import com.backend_sistema_clinico.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HorarioServiceImpl implements HorarioService {

    private final HorarioRepository horarioRepository;
    private final UserRepository userRepository;

    @Override
    public HorarioDTO create(CreateHorarioRequest request) {
        Usuario odontologo = userRepository.findById(request.getOdontologoId())
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        Horarios horario = Horarios.builder()
                .odontologo(odontologo)
                .diaSemana(request.getDiaSemana())
                .horaInicio(request.getHoraInicio())
                .horaFin(request.getHoraFin())
                .activo(true)
                .build();

        horarioRepository.save(horario);
        return toDTO(horario);
    }

    @Override
    public List<HorarioDTO> findByOdontologo(Long odontologoId) {
        return horarioRepository.findByOdontologoIdAndActivoTrue(odontologoId)
                .stream().map(this::toDTO).toList();
    }

    @Override
    public List<HorarioDTO> findByDia(DayOfWeek dia) {
        return horarioRepository.findByDiaSemanaAndActivoTrue(dia)
                .stream().map(this::toDTO).toList();
    }

    @Override
    public void delete(Long id) {
        Horarios horario = horarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
        horario.setActivo(false);
        horarioRepository.save(horario);
    }

    private HorarioDTO toDTO(Horarios h) {
        HorarioDTO dto = new HorarioDTO();
        dto.setId(h.getId());
        dto.setOdontologoId(h.getOdontologo().getId());
        dto.setOdontologoNombre(h.getOdontologo().getFirstName() + " " + h.getOdontologo().getLastName());
        dto.setDiaSemana(h.getDiaSemana());
        dto.setHoraInicio(h.getHoraInicio());
        dto.setHoraFin(h.getHoraFin());
        dto.setActivo(h.isActivo());
        return dto;
    }


}
