package com.recouvtech.recouvback.controller;

import com.recouvtech.recouvback.dto.CreanceDTO.CreanceRequestDTO;
import com.recouvtech.recouvback.dto.CreanceDTO.CreanceResponseDTO;
import com.recouvtech.recouvback.service.CreanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/creances")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CreanceController {

    private final CreanceService creanceService;

    @PostMapping
    public CreanceResponseDTO create(@RequestBody CreanceRequestDTO dto) {
        return creanceService.createCreance(dto);
    }

    @GetMapping
    public List<CreanceResponseDTO> getAll() {
        return creanceService.getAllCreances();
    }

    @GetMapping("/{numFacture}")
    public CreanceResponseDTO getByNumFacture(@PathVariable String numFacture) {
        return creanceService.getByNumFacture(numFacture);
    }

    @PutMapping("/{numFacture}")
    public CreanceResponseDTO update(@PathVariable String numFacture, @RequestBody CreanceRequestDTO dto) {
        return creanceService.updateCreance(numFacture, dto);
    }

    @DeleteMapping("/{numFacture}")
    public void delete(@PathVariable String numFacture) {
        creanceService.deleteCreance(numFacture);
    }
}
