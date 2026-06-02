package com.zhalgas.personalfinanceapi.auth;


import com.zhalgas.personalfinanceapi.user.User;
import com.zhalgas.personalfinanceapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwUtil jwUtil;
    private final PasswordEncoder passwordEncoder;

    public String register(AuthRequest authRequest) {
        if(userRepository.existByUsername(authRequest.getUsername())) {
            throw new RuntimeException("Username is already in use");
        }
        if(userRepository.existsByEmail(authRequest.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }
        User user = User.builder()
                .username(authRequest.getUsername())
                .email(authRequest.getEmail())
                .password(passwordEncoder.encode(authRequest.getPassword())) // Хэшируем пароль
                .build();
        userRepository.save(user);
        return "User registered successfully";
    }

    public AuthResponse login(AuthRequest authRequest) {
        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        String token = jwUtil.generateToken(user.getUsername());
        return new AuthResponse(token);
    }
}
