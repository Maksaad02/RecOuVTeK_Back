package com.recouvtech.recouvback.entity;

import com.recouvtech.recouvback.entity.enums.StatutCreance;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Creance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "num_facture", nullable = false, unique = true)
    private String numFacture;

    @Column(name = "date_emission")
    private LocalDate dateEmission;

    @Column(name = "echeance")
    private LocalDate echeance;

    @Column(name = "montant_facture")
    private Double montantFacture;

    @Column(name = "montant_encaisse")
    private Double montantEncaisse;

    @Column(name = "montant_penalites")
    private Double montantPenalites = 0.0;

    @Column(name = "date_calcul_penalites")
    private LocalDate dateCalculPenalites;

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
    public Double getSolde() {
        return (this.montantFacture + this.montantPenalites) - this.montantEncaisse;
    }

    @Transient
    public Double getMontantTotal() {
        return this.montantFacture + this.montantPenalites;
    }

    @Transient
    public int getJoursRetard() {
        if (this.echeance == null || LocalDate.now().isBefore(this.echeance)) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(this.echeance, LocalDate.now());
    }

    public void setMontantEncaisse(Double montantEncaisse) {
        this.montantEncaisse = montantEncaisse;
        updateStatut();
    }

    private void updateStatut() {
        if (this.montantEncaisse >= this.montantFacture) {
            this.statut = StatutCreance.PAYEE;
        } else if (this.montantEncaisse > 0) {
            this.statut = StatutCreance.PARTIELLEMENT_PAYEE;
        } else {
            // Calculer le statut basé sur le retard
            int joursRetard = getJoursRetard();
            if (joursRetard >= 60) {
                this.statut = StatutCreance.PENALISEE;
            } else if (joursRetard > 0) {
                this.statut = StatutCreance.EN_RETARD;
            } else {
                this.statut = StatutCreance.IMPAYEE;
            }
        }
    }
}