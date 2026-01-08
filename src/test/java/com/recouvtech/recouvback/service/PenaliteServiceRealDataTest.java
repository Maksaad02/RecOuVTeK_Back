package com.recouvtech.recouvback.service;

import com.recouvtech.recouvback.entity.Creance;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PenaliteServiceRealDataTest {

    @Test
    void testPenaltyCalculationWithRealData() {
        PenaliteService penaliteService = new PenaliteService();
        
        // Create creance with real data from Postman
        Creance creance = new Creance();
        creance.setId(3L);
        creance.setNumFacture("F2025-603");
        creance.setEcheance(LocalDate.of(2025, 6, 21)); // June 21, 2025
        creance.setMontantFacture(300000.0);
        creance.setMontantEncaisse(0.0);
        creance.setDateCalculPenalites(null); // No previous calculation
        
        // Test penalty calculation
        Double penalites = penaliteService.calculerPenalites(creance);
        int joursRetard = penaliteService.calculerJoursRetard(creance);
        
        System.out.println("=== Real Data Test ===");
        System.out.println("Due date: " + creance.getEcheance());
        System.out.println("Current date: " + LocalDate.now());
        System.out.println("Days overdue: " + joursRetard);
        System.out.println("Penalties calculated: " + penalites);
        
        // Expected: 91 days overdue, 1 month after 60-day threshold
        // Penalty: 300,000 * 0.0085 * 1 = 2,550 DH
        assertEquals(91, joursRetard);
        assertEquals(2550.0, penalites);
        
        // Test the update method
        penaliteService.mettreAJourPenalites(creance);
        System.out.println("After update - Penalties: " + creance.getMontantPenalites());
        System.out.println("After update - Calculation date: " + creance.getDateCalculPenalites());
        
        assertEquals(2550.0, creance.getMontantPenalites());
        assertEquals(LocalDate.now(), creance.getDateCalculPenalites());
    }
    
    @Test
    void testPenaltyCalculationWithCaching() {
        PenaliteService penaliteService = new PenaliteService();
        
        // Create creance with real data
        Creance creance = new Creance();
        creance.setId(3L);
        creance.setNumFacture("F2025-603");
        creance.setEcheance(LocalDate.of(2025, 6, 21));
        creance.setMontantFacture(300000.0);
        creance.setMontantEncaisse(0.0);
        creance.setDateCalculPenalites(LocalDate.now()); // Already calculated today
        creance.setMontantPenalites(0.0); // But penalties are 0
        
        System.out.println("=== Caching Test ===");
        System.out.println("Before update - Penalties: " + creance.getMontantPenalites());
        System.out.println("Before update - Calculation date: " + creance.getDateCalculPenalites());
        
        // This should NOT recalculate due to caching
        penaliteService.mettreAJourPenalites(creance);
        
        System.out.println("After update - Penalties: " + creance.getMontantPenalites());
        System.out.println("After update - Calculation date: " + creance.getDateCalculPenalites());
        
        // Penalties should still be 0 due to caching
        assertEquals(0.0, creance.getMontantPenalites());
        
        // Test force recalculation
        penaliteService.forcerRecalculPenalites(creance);
        System.out.println("After force recalculation - Penalties: " + creance.getMontantPenalites());
        
        // Now penalties should be calculated
        assertEquals(2550.0, creance.getMontantPenalites());
    }
}
