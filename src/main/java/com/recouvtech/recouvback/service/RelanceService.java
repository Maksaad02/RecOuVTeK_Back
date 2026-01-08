package com.recouvtech.recouvback.service;

import com.recouvtech.recouvback.dto.RelanceDTO.RelanceRequestDTO;
import com.recouvtech.recouvback.dto.RelanceDTO.RelanceResponseDTO;
import com.recouvtech.recouvback.entity.Creance;
import com.recouvtech.recouvback.entity.Relance;
import com.recouvtech.recouvback.entity.Utilisateur;
import com.recouvtech.recouvback.entity.enums.StatutRelance;
import com.recouvtech.recouvback.dao.CreanceRepository;
import com.recouvtech.recouvback.dao.RelanceRepository;
import com.recouvtech.recouvback.dao.UtilisateurRepository;
import com.recouvtech.recouvback.mapper.RelanceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelanceService {

    private final RelanceRepository relanceRepository;
    private final CreanceRepository creanceRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    private EmailService emailService;

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

    /**
     * Envoyer une relance manuellement (appelé par l'agent)
     */
    public boolean envoyerRelanceManuellement(Long relanceId, String agentName) {
        Relance relance = relanceRepository.findById(relanceId)
            .orElseThrow(() -> new RuntimeException("Relance non trouvée"));
        
        // Vérifier que la relance est en attente
        if (relance.getStatutRelance() != StatutRelance.EN_ATTENTE) {
            throw new RuntimeException("Cette relance ne peut pas être envoyée manuellement");
        }
        
        // Vérifier que la créance n'est pas payée
        if (relance.getCreance().getStatut().name().equals("PAYEE")) {
            relance.setStatutRelance(StatutRelance.ANNULEE);
            relance.setCommentaire("Créance déjà payée - relance annulée");
            relanceRepository.save(relance);
            throw new RuntimeException("Impossible d'envoyer la relance : créance déjà payée");
        }
        
        try {
            // Envoyer la relance
            emailService.envoyerRelance(relance);
            
            // Mettre à jour le statut
            relance.setStatutRelance(StatutRelance.EFFECTUEE);
            relance.setDateEnvoi(LocalDateTime.now());
            relance.setAgentEnvoi(agentName);
            relance.setCommentaire("Envoi manuel par " + agentName);
            
            relanceRepository.save(relance);
            
            log.info("Relance envoyée manuellement par " + agentName + 
                    " pour la créance: " + relance.getCreance().getNumFacture());
            
            return true;
            
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi manuel de la relance: " + e.getMessage());
            relance.setStatutRelance(StatutRelance.ANNULEE);
            relance.setCommentaire("Échec de l'envoi manuel: " + e.getMessage());
            relanceRepository.save(relance);
            
            return false;
        }
    }
    
    /**
     * Récupérer les relances en attente d'envoi
     */
    public List<RelanceResponseDTO> getRelancesEnAttente() {
        return relanceRepository.findByStatutRelance(StatutRelance.EN_ATTENTE)
            .stream()
            .map(RelanceMapper::toDto)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupérer les relances en attente pour une créance spécifique
     */
    public List<RelanceResponseDTO> getRelancesEnAttenteByCreance(String numFacture) {
        return relanceRepository.findByCreanceNumFactureAndStatutRelance(numFacture, StatutRelance.EN_ATTENTE)
            .stream()
            .map(RelanceMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Annuler les relances planifiées si la créance est payée
     */
    public void annulerRelancesPlanifiees(String numFacture) {
        List<Relance> relancesPlanifiees = relanceRepository
            .findByCreanceNumFactureAndStatutRelance(numFacture, StatutRelance.EN_ATTENTE);
        
        for (Relance relance : relancesPlanifiees) {
            relance.setStatutRelance(StatutRelance.ANNULEE);
            relance.setCommentaire("Créance payée - relance annulée");
            relanceRepository.save(relance);
        }
    }

    /**
     * Sauvegarder une relance
     */
    public Relance save(Relance relance) {
        return relanceRepository.save(relance);
    }
}