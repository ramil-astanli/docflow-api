package com.ramilastanli.docflow.security.controller;

import com.ramilastanli.docflow.entity.Role;
import com.ramilastanli.docflow.security.service.AuthService;
import com.ramilastanli.docflow.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;

    @PatchMapping("/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')") // Metod səviyyəsində təhlükəsizlik
    public ResponseEntity<String> updateRole(
            @PathVariable Long userId,
            @RequestParam Role role) {

        Long currentAdminId = SecurityUtils.getCurrentUserId();

        if(currentAdminId.equals(userId)){
            return ResponseEntity.badRequest().body("Admin oz rolunu deyise bilmez");
        }

        authService.changeUserRole(userId, role);
        return ResponseEntity.ok("İstifadəçinin rolu uğurla " + role + " olaraq dəyişdirildi.");
    }
}