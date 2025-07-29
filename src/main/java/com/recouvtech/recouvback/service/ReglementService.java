package com.recouvtech.recouvback.service;

import com.recouvtech.recouvback.dto.ReglementDTO.ReglementRequestDTO;
import com.recouvtech.recouvback.dto.ReglementDTO.ReglementResponseDTO;
import com.recouvtech.recouvback.entity.Creance;
import com.recouvtech.recouvback.entity.Reglement;
import com.recouvtech.recouvback.entity.Utilisateur;
import com.recouvtech.recouvback.dao.CreanceRepository;
import com.recouvtech.recouvback.dao.ReglementRepository;
import com.recouvtech.recouvback.dao.UtilisateurRepository;
import com.recouvtech.recouvback.entity.enums.StatutReglement;
import com.recouvtech.recouvback.mapper.ReglementMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReglementService {

    private final ReglementRepository reglementRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CreanceRepository creanceRepository;

    @Transactional
    public ReglementResponseDTO create(ReglementRequestDTO dto) {
        Creance creance = creanceRepository.findByNumFacture(dto.getNumFacture());
        if (creance == null) {
            throw new RuntimeException("Créance not found with numFacture: " + dto.getNumFacture());
        }

        Utilisateur agent = utilisateurRepository.findByNom(dto.getAgentName());
        if (agent == null) {
            throw new RuntimeException("Agent not found with name: " + dto.getAgentName());
        }

        Reglement r = ReglementMapper.fromRequestDto(dto, creance, agent);
        r.setStatut(dto.getStatut() != null ? dto.getStatut() : StatutReglement.NON_EFFECTUE);

        // Sauvegarde du règlement
        Reglement saved = reglementRepository.save(r);

        // Mise à jour du montant encaissé seulement si le statut est EFFECTUE
        if (r.getStatut() == StatutReglement.EFFECTUE) {
            updateCreanceMontantEncaisse(creance);
        }

        return ReglementMapper.toDto(saved);
    }

    public List<ReglementResponseDTO> getAll() {
        return reglementRepository.findAll().stream().map(ReglementMapper::toDto).collect(Collectors.toList());
    }

    public ReglementResponseDTO getById(Long id) {
        return reglementRepository.findById(id).map(ReglementMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Reglement not found with id: " + id));
    }

    @Transactional
    public ReglementResponseDTO update(Long id, ReglementRequestDTO dto) {
        Reglement r = reglementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reglement not found with id: " + id));

        boolean wasEffectue = r.getStatut() == StatutReglement.EFFECTUE;
        StatutReglement newStatut = dto.getStatut() != null ? dto.getStatut() : r.getStatut();
        boolean willBeEffectue = newStatut == StatutReglement.EFFECTUE;

        Creance creance = creanceRepository.findByNumFacture(dto.getNumFacture());
        if (creance == null) {
            throw new RuntimeException("Créance not found with numFacture: " + dto.getNumFacture());
        }
        
        Utilisateur agent = utilisateurRepository.findByNom(dto.getAgentName());
        if (agent == null) {
            throw new RuntimeException("Agent not found with name: " + dto.getAgentName());
        }

        ReglementMapper.updateFromRequestDto(r, dto, creance, agent);
        r.setStatut(newStatut);

        Reglement saved = reglementRepository.save(r);

        // Mise à jour du montant encaissé si le statut a changé vers ou depuis EFFECTUE
        if (wasEffectue != willBeEffectue) {
            updateCreanceMontantEncaisse(creance);
        }

        return ReglementMapper.toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        Reglement reglement = reglementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reglement not found with id: " + id));
        Creance creance = reglement.getCreance();
        
        reglementRepository.deleteById(id);
        
        // Recalculate montantEncaisse only if the deleted payment was EFFECTUE
        if (reglement.getStatut() == StatutReglement.EFFECTUE) {
            updateCreanceMontantEncaisse(creance);
        }
    }

    @Transactional
    public ReglementResponseDTO updateStatus(Long id, StatutReglement newStatus) {
        Reglement reglement = reglementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reglement not found with id: " + id));
        
        boolean statusChanged = reglement.getStatut() != newStatus;
        reglement.setStatut(newStatus);
        
        Reglement saved = reglementRepository.save(reglement);
        
        // Update montantEncaisse only if status changed to or from EFFECTUE
        if (statusChanged && (newStatus == StatutReglement.EFFECTUE || reglement.getStatut() == StatutReglement.EFFECTUE)) {
            updateCreanceMontantEncaisse(reglement.getCreance());
        }
        
        return ReglementMapper.toDto(saved);
    }

    private void updateCreanceMontantEncaisse(Creance creance) {
        double totalEncaisse = reglementRepository.findByCreance(creance).stream()
                .filter(r -> r.getStatut() == StatutReglement.EFFECTUE)
                .mapToDouble(Reglement::getMontant)
                .sum();
                
        creance.setMontantEncaisse(totalEncaisse);
        creanceRepository.save(creance);
    }
}