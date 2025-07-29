package com.recouvtech.recouvback.dao;

import com.recouvtech.recouvback.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);

    Utilisateur findByNom(String agentName);
}