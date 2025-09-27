package com.recouvtech.recouvback.service;

import com.recouvtech.recouvback.entity.Creance;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class PenaliteService {
    
    private static final int SEUIL_PENALISATION = 60; // jours
    private static final Double TAUX_PENALITE_MENSUELLE = 0.0085; // 0.85% par mois
    
    /**
     * Calcule les pénalités pour une créance
     * @param creance La créance à traiter
     * @return Le montant des pénalités calculées
     */
    public Double calculerPenalites(Creance creance) {
        if (creance.getEcheance() == null) {
            return 0.0;
        }
        
        int joursRetard = calculerJoursRetard(creance);
        
        // Pas de pénalités si moins de 60 jours de retard
        if (joursRetard < SEUIL_PENALISATION) {
            return 0.0;
        }
        
        // Calculer le nombre de mois complets après 60 jours
        int moisApresSeuil = (joursRetard - SEUIL_PENALISATION) / 30;
        
        // Appliquer la pénalité mensuelle sur le montant initial de la facture
        Double penalites = creance.getMontantFacture() * TAUX_PENALITE_MENSUELLE * moisApresSeuil;
        
        return Math.round(penalites * 100.0) / 100.0; // Arrondir à 2 décimales
    }
    
    /**
     * Calcule le nombre de jours de retard
     * @param creance La créance à traiter
     * @return Le nombre de jours de retard
     */
    public int calculerJoursRetard(Creance creance) {
        if (creance.getEcheance() == null || LocalDate.now().isBefore(creance.getEcheance())) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(creance.getEcheance(), LocalDate.now());
    }
    
    /**
     * Met à jour les pénalités d'une créance si nécessaire
     * @param creance La créance à mettre à jour
     */
    public void mettreAJourPenalites(Creance creance) {
        LocalDate aujourdhui = LocalDate.now();
        
        // Vérifier si un recalcul est nécessaire
        // Recalculer si jamais calculé ou si les pénalités sont 0 mais qu'il devrait y en avoir
        if (creance.getDateCalculPenalites() != null && 
            creance.getDateCalculPenalites().equals(aujourdhui) &&
            creance.getMontantPenalites() != null && 
            creance.getMontantPenalites() > 0) {
            return; // Déjà calculé aujourd'hui et pénalités > 0
        }
        
        Double nouvellesPenalites = calculerPenalites(creance);
        creance.setMontantPenalites(nouvellesPenalites);
        creance.setDateCalculPenalites(aujourdhui);
    }
    
    /**
     * Force le recalcul des pénalités pour une créance
     * @param creance La créance à recalculer
     */
    public void forcerRecalculPenalites(Creance creance) {
        Double nouvellesPenalites = calculerPenalites(creance);
        creance.setMontantPenalites(nouvellesPenalites);
        creance.setDateCalculPenalites(LocalDate.now());
    }
} 