package com.recouvtech.recouvback.service;

import com.recouvtech.recouvback.dao.RoleRepository;
import com.recouvtech.recouvback.entity.Role;
import com.recouvtech.recouvback.entity.enums.RoleAgent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer {

    private final RoleRepository roleRepository;

    @PostConstruct
    public void initRoles() {
        if (roleRepository.count() == 0) {

            Role admin = new Role();
            admin.setNom(RoleAgent.ADMIN);

            Role agent = new Role();
            agent.setNom(RoleAgent.AGENT);

            roleRepository.save(admin);
            roleRepository.save(agent);
        }
    }
}
