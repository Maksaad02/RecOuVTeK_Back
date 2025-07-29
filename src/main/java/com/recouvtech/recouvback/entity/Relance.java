package com.recouvtech.recouvback.entity;

import com.recouvtech.recouvback.entity.enums.StatutRelance;
import com.recouvtech.recouvback.entity.enums.TypeRelance;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;


@Getter
@Setter
@Entity
@Data
public class Relance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    public Creance creance;


    @ManyToOne(optional = false)
    @JoinColumn(name = "id_agent_recouv", nullable = false)
    private Utilisateur agentRecouv;

    public LocalDate dateRelance;

    @Enumerated(EnumType.STRING)
    private TypeRelance typeRelance;

    @Enumerated(EnumType.STRING)
    private StatutRelance statutRelance;
    public String commentaire;



}

