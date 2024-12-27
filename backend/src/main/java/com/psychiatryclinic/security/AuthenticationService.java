package com.psychiatryclinic.security;

import com.psychiatryclinic.business.requests.auth.LoginRequest;
import com.psychiatryclinic.business.responses.auth.AuthenticationResponse;
import com.psychiatryclinic.dataAccess.UserRepository;
import com.psychiatryclinic.entities.User;
import com.psychiatryclinic.core.exceptions.AuthenticationException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import java.util.Collections;
import com.psychiatryclinic.entities.enums.Role;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String googleClientId;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${google.client.id}") String googleClientId) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.googleClientId = googleClientId;
    }

    public AuthenticationResponse authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new AuthenticationException("Geçersiz email veya şifre"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Geçersiz email veya şifre");
        }

        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse authenticateWithGoogle(String credential) {
        try {
            // Google token'ını doğrula
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

            GoogleIdToken idToken = verifier.verify(credential);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                
                // Kullanıcıyı veritabanında bul veya oluştur
                User user = userRepository.findByEmail(email)
                    .orElseGet(() -> createGoogleUser(payload));
                
                // JWT token oluştur
                String token = jwtService.generateToken(user);
                
                return new AuthenticationResponse(token);
            } else {
                throw new AuthenticationException("Google token doğrulanamadı");
            }
        } catch (Exception e) {
            throw new AuthenticationException("Google authentication hatası: " + e.getMessage());
        }
    }
    
    private User createGoogleUser(GoogleIdToken.Payload payload) {
        User user = new User();
        user.setEmail(payload.getEmail());
        user.setFirstName((String) payload.get("given_name"));
        user.setLastName((String) payload.get("family_name"));
        user.setRole(Role.PATIENT); // Varsayılan rol
        user.setActive(true);
        
        return userRepository.save(user);
    }
} 