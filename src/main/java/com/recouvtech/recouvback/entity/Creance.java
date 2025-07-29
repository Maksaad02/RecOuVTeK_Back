package com.recouvtech.recouvback.entity;

import com.recouvtech.recouvback.entity.enums.StatutCreance;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Creance {

    @Id
    @Column(name = "num_facture", nullable = false, unique = true)
    private String numFacture;

    @Column(name = "date_emission")
    private LocalDate dateEmission;

    @Column(name = "echeance")
    private LocalDate echeance;

    @Column(name = "montant_facture")
    private double montantFacture;

    @Column(name = "montant_encaisse")
    private double montantEncaisse;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutCreance statut;

    @ManyToOne
    @JoinColumn(name = "agent_recouv")
    private Utilisateur agentRecouv;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "creance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reglement> reglements = new ArrayList<>();

    @Transient
    public double getSolde() {
        return this.montantFacture - this.montantEncaisse;
    }

    public void setMontantEncaisse(double montantEncaisse) {
        this.montantEncaisse = montantEncaisse;
        updateStatut();
    }

    private void updateStatut() {
        if (this.montantEncaisse >= this.montantFacture) {
            this.statut = StatutCreance.PAYEE;
        } else if (this.montantEncaisse > 0) {
            this.statut = StatutCreance.PARTIELLEMENT_PAYEE;
        } else if (LocalDate.now().isAfter(this.echeance)) {
            this.statut = StatutCreance.EN_RETARD;
        } else {
            this.statut = StatutCreance.IMPAYEE;
        }
    }
}
