package com.recouvtech.recouvback.service;


import com.recouvtech.recouvback.dto.RelanceDTO.RelanceRequestDTO;
import com.recouvtech.recouvback.dto.RelanceDTO.RelanceResponseDTO;
import com.recouvtech.recouvback.entity.Creance;
import com.recouvtech.recouvback.entity.Relance;
import com.recouvtech.recouvback.entity.Utilisateur;
import com.recouvtech.recouvback.dao.CreanceRepository;
import com.recouvtech.recouvback.dao.RelanceRepository;
import com.recouvtech.recouvback.dao.UtilisateurRepository;
import com.recouvtech.recouvback.mapper.RelanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelanceService {


    private final RelanceRepository relanceRepository;
    private final CreanceRepository creanceRepository;
    private final UtilisateurRepository utilisateurRepository;

    public RelanceResponseDTO create(RelanceRequestDTO dto) {
        Creance creance = creanceRepository.findByNumFacture(dto.getNumFacture());
        Utilisateur agent = utilisateurRepository.findByNom(dto.getAgentName());
        if (agent == null) {
            throw new RuntimeException("Aucun utilisateur trouvé avec le nom : " + dto.getAgentName());
        }
        Relance r = RelanceMapper.fromRequestDto(dto, creance, agent);
        return RelanceMapper.toDto(relanceRepository.save(r));
    }

    public List<RelanceResponseDTO> getAll() {
        return relanceRepository.findAll().stream().map(RelanceMapper::toDto).collect(Collectors.toList());
    }

    public RelanceResponseDTO getById(Long id) {
        return relanceRepository.findById(id).map(RelanceMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    public RelanceResponseDTO update(Long id, RelanceRequestDTO dto) {
        Relance r = relanceRepository.findById(id).orElseThrow();
        Creance creance = creanceRepository.findByNumFacture(dto.getNumFacture());
        Utilisateur agent = utilisateurRepository.findByNom(dto.getAgentName());
        RelanceMapper.updateFromRequestDto(r, dto, creance, agent);
        return RelanceMapper.toDto(relanceRepository.save(r));
    }

    public void delete(Long id) {
        relanceRepository.deleteById(id);
    }

}