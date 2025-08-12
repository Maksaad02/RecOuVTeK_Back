package com.recouvtech.recouvback.dto.CreanceDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreanceResponseDTO {
    private Long id;
    private String numFacture;
    private LocalDate echeance;
    private Double montantFacture;
    private Double montantEncaisse;
    private Double solde;
    private Double montantPenalites;
    private Double montantTotal;
    private int joursRetard;
    private String statut;
    private String agentName;
    private String clientName;
}