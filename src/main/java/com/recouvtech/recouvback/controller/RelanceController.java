package com.recouvtech.recouvback.controller;


import com.recouvtech.recouvback.dto.RelanceDTO.RelanceRequestDTO;
import com.recouvtech.recouvback.dto.RelanceDTO.RelanceResponseDTO;
import com.recouvtech.recouvback.service.RelanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relances")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081")
public class RelanceController {

    private final RelanceService relanceService;

    @PostMapping
    public RelanceResponseDTO create(@RequestBody RelanceRequestDTO dto) {
        return relanceService.create(dto);
    }

    @GetMapping
    public List<RelanceResponseDTO> getAll() {
        return relanceService.getAll();
    }

    @GetMapping("/{id}")
    public RelanceResponseDTO getById(@PathVariable Long id) {
        return relanceService.getById(id);
    }

    @PutMapping("/{id}")
    public RelanceResponseDTO update(@PathVariable Long id, @RequestBody RelanceRequestDTO dto) {
        return relanceService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        relanceService.delete(id);
    }
}
