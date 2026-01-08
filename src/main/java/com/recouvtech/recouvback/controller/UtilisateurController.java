package com.recouvtech.recouvback.controller;

import com.recouvtech.recouvback.dto.UtilisateurDTO.UtilisateurRequestDTO;
import com.recouvtech.recouvback.dto.UtilisateurDTO.UtilisateurResponseDTO;
import com.recouvtech.recouvback.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @PostMapping
    public ResponseEntity<UtilisateurResponseDTO> create(@RequestBody UtilisateurRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(utilisateurService.create(dto));
    }


    @GetMapping("/me")
    public ResponseEntity<UtilisateurResponseDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(utilisateurService.getByEmail(userDetails.getUsername()));
    }

    
    @GetMapping
    public List<UtilisateurResponseDTO> getAll() {
        return utilisateurService.getAll();
    }

    @GetMapping("/{id}")
    public UtilisateurResponseDTO getById(@PathVariable Long id) {
        return utilisateurService.getById(id);
    }

    @PutMapping("/{id}")
    public UtilisateurResponseDTO update(@PathVariable Long id, @RequestBody UtilisateurRequestDTO dto) {
        return utilisateurService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        utilisateurService.delete(id);
    }

    @PutMapping("/{id}/role") /**/
    public UtilisateurResponseDTO updateRole(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        return utilisateurService.updateRole(id, payload.get("role"));
    }
}