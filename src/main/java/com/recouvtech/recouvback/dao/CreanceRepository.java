package com.recouvtech.recouvback.dao;

import com.recouvtech.recouvback.entity.Creance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreanceRepository extends JpaRepository<Creance, String> {
    Creance findByNumFacture(String numFacture);
}
