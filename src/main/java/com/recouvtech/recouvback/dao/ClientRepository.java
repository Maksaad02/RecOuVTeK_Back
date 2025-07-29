package com.recouvtech.recouvback.dao;

import com.recouvtech.recouvback.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findByRaisonSociale(String clientName);
}
