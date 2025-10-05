package com.recouvtech.recouvback.service;

import com.recouvtech.recouvback.dao.RoleRepository;
import com.recouvtech.recouvback.dao.UtilisateurRepository;
import com.recouvtech.recouvback.dto.UtilisateurDTO.UtilisateurRequestDTO;
import com.recouvtech.recouvback.dto.UtilisateurDTO.UtilisateurResponseDTO;
import com.recouvtech.recouvback.entity.Role;
import com.recouvtech.recouvback.entity.Utilisateur;
import com.recouvtech.recouvback.entity.enums.RoleAgent;
import com.recouvtech.recouvback.mapper.UtilisateurMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurResponseDTO create(UtilisateurRequestDTO dto) {
        // Vérification email unique
        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));
        Utilisateur utilisateur = UtilisateurMapper.fromRequestDto(dto, role);
        // Encode password before saving
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        return UtilisateurMapper.toDto(utilisateurRepository.save(utilisateur));
    }

    public List<UtilisateurResponseDTO> getAll() {
        return utilisateurRepository.findAll().stream()
                .map(UtilisateurMapper::toDto)
                .collect(Collectors.toList());
    }

    public UtilisateurResponseDTO getById(Long id) {
        return utilisateurRepository.findById(id)
                .map(UtilisateurMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found"));
    }

    public UtilisateurResponseDTO update(Long id, UtilisateurRequestDTO dto) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found"));

        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        UtilisateurMapper.updateFromRequestDto(utilisateur, dto, role);
        // Encode updated password as well
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        return UtilisateurMapper.toDto(utilisateurRepository.save(utilisateur));
    }

    public void delete(Long id) {
        utilisateurRepository.deleteById(id);
    }

    public UtilisateurResponseDTO getByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .map(UtilisateurMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
    /**/
    public UtilisateurResponseDTO updateRole(Long id, String roleName) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        RoleAgent nouveauRole = RoleAgent.valueOf(roleName.toUpperCase());
        Role role = roleRepository.findByNom(nouveauRole)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));

        utilisateur.setRole(role);
        return UtilisateurMapper.toDto(utilisateurRepository.save(utilisateur));
    }


}