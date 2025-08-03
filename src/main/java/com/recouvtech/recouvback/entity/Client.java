package com.recouvtech.recouvback.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "raison_sociale")
    private String raisonSociale;

    @Column(name = "email")
    private String email;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "rc")
    private String rc;

    @Column(name = "adresse")
    private String adresse;

    @Column(name = "ice")
    private String ice;

    @Column(name = "identite_fiscale")
    private String identiteFiscale;

    @ManyToOne
    @JoinColumn(name = "id_agent_recouv")
    private Utilisateur agentRecouv;
}
