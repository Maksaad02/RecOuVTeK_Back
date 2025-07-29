package com.recouvtech.recouvback.dto.CreanceDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreanceResponseDTO {
    private Long id;
    private String numFacture;
    private LocalDate echeance;
    private double montantFacture;
    private double montantEncaisse;
    private double solde;
    private String statut;
    private String agentName;
    private String clientName;
}