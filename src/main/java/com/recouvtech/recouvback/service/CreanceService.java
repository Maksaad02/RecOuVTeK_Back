package com.recouvtech.recouvback.service;

import com.recouvtech.recouvback.dao.ClientRepository;
import com.recouvtech.recouvback.dao.CreanceRepository;
import com.recouvtech.recouvback.dao.UtilisateurRepository;
import com.recouvtech.recouvback.dto.CreanceDTO.CreanceRequestDTO;
import com.recouvtech.recouvback.dto.CreanceDTO.CreanceResponseDTO;
import com.recouvtech.recouvback.entity.Client;
import com.recouvtech.recouvback.entity.Creance;
import com.recouvtech.recouvback.entity.Utilisateur;
import com.recouvtech.recouvback.entity.enums.StatutCreance;
import com.recouvtech.recouvback.mapper.CreanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreanceService {

    private final CreanceRepository creanceRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ClientRepository clientRepository;

    public CreanceResponseDTO createCreance(CreanceRequestDTO dto) {

        if (creanceRepository.existsById(dto.getNumFacture())) {
            throw new RuntimeException("Une créance avec ce numéro de facture existe déjà : " + dto.getNumFacture());
        }

        Utilisateur agent = utilisateurRepository.findByNom(dto.getAgentName());
        if (agent == null) {
            throw new RuntimeException("Agent not found with name: " + dto.getAgentName());
        }

        Client client = clientRepository.findByRaisonSociale(dto.getClientName());
        if (client == null) {
            throw new RuntimeException("Client not found with name: " + dto.getClientName());
        }

        Creance creance = CreanceMapper.fromRequestDto(dto, agent, client);
        creance.setMontantEncaisse(0.0); // initialise à 0

        return CreanceMapper.toDto(creanceRepository.save(creance));
    }

    public List<CreanceResponseDTO> getAllCreances() {
        return creanceRepository.findAll().stream()
                .map(CreanceMapper::toDto).collect(Collectors.toList());
    }

    public CreanceResponseDTO getByNumFacture(String numFacture) {
        return creanceRepository.findById(numFacture)
                .map(CreanceMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Créance non trouvée pour la facture : " + numFacture));
    }

    public CreanceResponseDTO updateCreance(String numFacture, CreanceRequestDTO dto) {
        Creance creance = creanceRepository.findById(numFacture)
                .orElseThrow(() -> new RuntimeException("Créance non trouvée pour : " + numFacture));

        Utilisateur agent = utilisateurRepository.findByNom(dto.getAgentName());
        Client client = clientRepository.findByRaisonSociale(dto.getClientName());
        
        CreanceMapper.updateFromRequestDto(creance, dto, agent, client);
        // montantEncaisse est mis à jour depuis les règlements uniquement

        return CreanceMapper.toDto(creanceRepository.save(creance));
    }

    public void deleteCreance(String numFacture) {
        creanceRepository.deleteById(numFacture);
    }
}
