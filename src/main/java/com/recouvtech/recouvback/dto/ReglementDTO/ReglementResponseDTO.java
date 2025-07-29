package com.recouvtech.recouvback.dto.ReglementDTO;

import com.recouvtech.recouvback.entity.enums.ModePaiement;
import com.recouvtech.recouvback.entity.enums.StatutReglement;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReglementResponseDTO {
    private Long id;
    private double montant;
    private LocalDate dateReglement;
    private ModePaiement modePaiement;
    private StatutReglement statut;
    private String reference;
    private String numFacture;
    private String agentName;
    private String clientName;
}
