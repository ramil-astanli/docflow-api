package com.ramilastanli.docflow.security.controller;

import com.ramilastanli.docflow.security.service.AuthService;
import com.ramilastanli.docflow.security.dto.request.LoginRequestDto;
import com.ramilastanli.docflow.security.dto.response.LoginResponseDto;
import com.ramilastanli.docflow.security.dto.request.RegisterRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login/public")
    public ResponseEntity<LoginResponseDto> apiLogin(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        try {
            log.info("Giriş cəhdi edilir: {}", loginRequestDto.email());
            LoginResponseDto response = authService.login(loginRequestDto);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ex) {
            log.error("Giriş uğursuzdur: Yanlış məlumatlar - {}", loginRequestDto.email());
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Yanlış email və ya şifrə!");
        } catch (AuthenticationException ex) {
            log.error("Autentifikasiya xətası: {}", ex.getMessage());
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Giriş rədd edildi!");
        } catch (Exception ex) {
            log.error("Gözlənilməz xəta: {}", ex.getMessage());
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Sistem xətası: " + ex.getMessage());
        }
    }

    @PostMapping("/register/public")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        try {
            log.info("Yeni qeydiyyat cəhdi: {}", registerRequestDto.email());
            authService.register(registerRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("İstifadəçi uğurla qeydiyyatdan keçdi!");
        } catch (RuntimeException ex) {
            log.warn("Qeydiyyat xətası: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            log.error("Qeydiyyat zamanı gözlənilməz xəta: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Xəta baş verdi: " + ex.getMessage());
        }
    }

    private ResponseEntity<LoginResponseDto> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new LoginResponseDto(message, null, null));
    }
}