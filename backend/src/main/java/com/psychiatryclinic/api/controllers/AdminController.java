package com.psychiatryclinic.api.controllers;

import com.psychiatryclinic.business.concretes.UserManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    
    private final UserManager userManager;
    
    @PostMapping("/sync-user-roles")
    public ResponseEntity<String> syncUserRoles() {
        userManager.syncUserRoles();
        return ResponseEntity.ok("Roller senkronize edildi");
    }
} 