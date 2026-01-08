package com.recouvtech.recouvback.service;

import com.recouvtech.recouvback.dao.RoleRepository;
import com.recouvtech.recouvback.dao.UtilisateurRepository;
import com.recouvtech.recouvback.dto.UtilisateurDTO.UtilisateurRequestDTO;
import com.recouvtech.recouvback.dto.UtilisateurDTO.UtilisateurResponseDTO;
import com.recouvtech.recouvback.entity.Role;
import com.recouvtech.recouvback.entity.Utilisateur;
import com.recouvtech.recouvback.entity.enums.RoleAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UtilisateurService utilisateurService;

    private Utilisateur utilisateur;
    private UtilisateurRequestDTO requestDTO;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setNom(RoleAgent.AGENT);

        utilisateur = new Utilisateur();
        utilisateur.setIdAgentRecouv(1L);
        utilisateur.setNom("John Doe");
        utilisateur.setEmail("john@example.com");
        utilisateur.setMotDePasse("password123");
        utilisateur.setRole(role);

        requestDTO = new UtilisateurRequestDTO();
        requestDTO.setNom("Jane Doe");
        requestDTO.setEmail("jane@example.com");
        requestDTO.setMotDePasse("password456");
        requestDTO.setRoleId(1L);
    }

    @Test
    void testCreate() {
        // Given
        when(utilisateurRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(roleRepository.findById(requestDTO.getRoleId())).thenReturn(Optional.of(role));
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);

        // When
        UtilisateurResponseDTO result = utilisateurService.create(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(utilisateur.getIdAgentRecouv(), result.getId());
        assertEquals(utilisateur.getNom(), result.getNom());
        assertEquals(utilisateur.getEmail(), result.getEmail());
        verify(utilisateurRepository).existsByEmail(requestDTO.getEmail());
        verify(roleRepository).findById(requestDTO.getRoleId());
        verify(utilisateurRepository).save(any(Utilisateur.class));
    }

    @Test
    void testCreateWithExistingEmail() {
        // Given
        when(utilisateurRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> utilisateurService.create(requestDTO));
        verify(utilisateurRepository).existsByEmail(requestDTO.getEmail());
        verify(utilisateurRepository, never()).save(any());
    }

    @Test
    void testGetAll() {
        // Given
        List<Utilisateur> utilisateurs = Arrays.asList(utilisateur);
        when(utilisateurRepository.findAll()).thenReturn(utilisateurs);

        // When
        List<UtilisateurResponseDTO> result = utilisateurService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(utilisateur.getIdAgentRecouv(), result.get(0).getId());
        verify(utilisateurRepository).findAll();
    }

    @Test
    void testGetById() {
        // Given
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));

        // When
        UtilisateurResponseDTO result = utilisateurService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(utilisateur.getIdAgentRecouv(), result.getId());
        verify(utilisateurRepository).findById(1L);
    }

    @Test
    void testGetByIdNotFound() {
        // Given
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> utilisateurService.getById(1L));
        verify(utilisateurRepository).findById(1L);
    }
} 