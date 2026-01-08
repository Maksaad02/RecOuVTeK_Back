package com.recouvtech.recouvback.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "raison_sociale", nullable = false, unique = true)
    private String raisonSociale;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "telephone", nullable = false, unique = true)
    private String telephone;

    @Column(name = "rc", nullable = false, unique = true)
    private String rc;

    @Column(name = "adresse", nullable = false, unique = true)
    private String adresse;

    @Column(name = "ice", nullable = false, unique = true)
    private String ice;

    @Column(name = "identite_fiscale", nullable = false, unique = true)
    private String identiteFiscale;

    @ManyToOne
    @JoinColumn(name = "id_agent_recouv")
    private Utilisateur agentRecouv;
}
