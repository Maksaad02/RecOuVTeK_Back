package com.recouvtech.recouvback.controller;


import com.recouvtech.recouvback.entity.Role;
import com.recouvtech.recouvback.entity.enums.RoleAgent;
import com.recouvtech.recouvback.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/ajouterRole")
    public Role postRole(@RequestBody Role role) {
        return roleService.postRole(role);
    }

    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @PatchMapping("/updateRole/{id}")
    public ResponseEntity<Role> updateRole(
            @PathVariable Long id,
            @RequestBody Role roleUpdates) {

        Role updatedRole = roleService.updateRole(id, roleUpdates);
        return ResponseEntity.ok(updatedRole);
    }





    @DeleteMapping("/delRole/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable long id){
        try{
            roleService.deleteRole(id);
            return new ResponseEntity<>("Role with id " + id + " deleted", HttpStatus.OK);
        } catch (EntityNotFoundException e){
            return new ResponseEntity<>("Role with id " + id + " not found", HttpStatus.NOT_FOUND);
        }
    }

}
