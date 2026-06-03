package com.zhalgas.personalfinanceapi.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody AuthRequest request){
        String message = authService.register(request);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request){
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
