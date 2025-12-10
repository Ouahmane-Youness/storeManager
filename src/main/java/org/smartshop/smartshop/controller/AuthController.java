package org.smartshop.smartshop.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smartshop.smartshop.dto.auth.LoginRequestDTO;
import org.smartshop.smartshop.dto.auth.LoginResponseDTO;
import org.smartshop.smartshop.service.interf.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto,
            HttpSession session) {
        LoginResponseDTO response = authService.login(dto, session);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session)
    {
        authService.logout(session);
        return ResponseEntity.ok(Map.of("message", "Logout Successful"));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus(HttpSession session) {
        boolean authenticated = authService.isAuthenticated(session);

        if (authenticated) {
            return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "userId", session.getAttribute("userId"),
                    "usernNme", session.getAttribute("userName"),
                    "userRole", session.getAttribute("userRole")
            ));
        }

        return ResponseEntity.ok(Map.of("authenticated", false));
    }
}
