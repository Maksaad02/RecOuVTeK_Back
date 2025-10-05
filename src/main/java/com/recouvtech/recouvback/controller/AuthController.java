package com.recouvtech.recouvback.controller;

import com.recouvtech.recouvback.configuration.JwtUtils;
import com.recouvtech.recouvback.dao.RoleRepository;
import com.recouvtech.recouvback.dao.UtilisateurRepository;
import com.recouvtech.recouvback.entity.Role;
import com.recouvtech.recouvback.entity.Utilisateur;
import com.recouvtech.recouvback.entity.enums.RoleAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UtilisateurRepository utilisateurRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> register(@RequestBody Utilisateur u) {
        if (utilisateurRepo.existsByEmail(u.getEmail())) {
            return ResponseEntity.badRequest().body("Email déjà utilisé");
        }
        u.setMotDePasse(passwordEncoder.encode(u.getMotDePasse()));
        Role role = roleRepo.findByNom(RoleAgent.AGENT)
                .orElseThrow(() -> new RuntimeException("Rôle AGENT introuvable"));
        u.setRole(role);
        utilisateurRepo.save(u);
        return ResponseEntity.ok("Utilisateur créé");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        Utilisateur user = utilisateurRepo.findByEmail(req.username())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        String token = jwtUtils.generateToken(req.username());

        return ResponseEntity.ok(new JwtResponse(token, user.getIdAgentRecouv(), user.getNom(), user.getEmail(), /**/ user.getRole().getNom().name().toLowerCase()));
    }

    record LoginRequest(String username, String password) {}
    record JwtResponse(String token, Long id, String name, String email, String role) {}
}

