package com.recouvtech.recouvback.dto.CreanceDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreanceRequestDTO {
    private String numFacture;
    private LocalDate dateEmission;
    private LocalDate echeance;
    private Double montantFacture;
    private Double montantEncaisse;
    private String statut; // Enum sous forme de String
    private String agentName;
    private String clientName; // raisonSociale
}
