package com.psychiatryclinic.api.controllers;

import com.psychiatryclinic.business.requests.auth.LoginRequest;
import com.psychiatryclinic.business.requests.auth.RegisterRequest;
import com.psychiatryclinic.business.responses.auth.AuthResponse;
import com.psychiatryclinic.business.responses.auth.AuthenticationResponse;
import com.psychiatryclinic.core.exceptions.BusinessException;
import com.psychiatryclinic.core.exceptions.ErrorResponse;
import com.psychiatryclinic.dataAccess.UserRepository;
import com.psychiatryclinic.dataAccess.PatientRepository;
import com.psychiatryclinic.entities.User;
import com.psychiatryclinic.entities.enums.Role;
import com.psychiatryclinic.security.JwtService;
import com.psychiatryclinic.security.GoogleTokenVerifier;
import com.psychiatryclinic.security.AuthenticationService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.psychiatryclinic.api.services.EmailService;
import com.psychiatryclinic.api.dto.EmailRequest;
import org.springframework.security.core.AuthenticationException;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Kimlik Doğrulama", description = "Kullanıcı giriş ve kayıt işlemleri")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@AllArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final GoogleTokenVerifier verifier;
    private final AuthenticationService authenticationService;
    private final EmailService emailService;

    @PostMapping("/login")
    @Operation(
        summary = "Kullanıcı Girişi",
        description = "Email ve şifre ile giriş yaparak JWT token alır",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Başarılı giriş",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AuthResponse.class),
                    examples = @ExampleObject(
                        value = """
                            {
                                "token": "eyJhbGciOiJIUzI1NiJ9..."
                            }
                            """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Geçersiz kimlik bilgileri",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        value = """
                            {
                                "status": 401,
                                "message": "Geçersiz email veya şifre",
                                "timestamp": 1703127600000
                            }
                            """
                    )
                )
            )
        }
    )
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        logger.info("Giriş isteği alındı - Email: {}", request.getEmail());
        
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Kullanıcı bulunamadı"));
            
            String token = jwtService.generateToken(user);
            logger.info("Kullanıcı başarıyla giriş yaptı: {}", user.getEmail());
            
            return ResponseEntity.ok(new AuthResponse(
                token, 
                user.getId(),
                user.getEmail(),
                user.getFirstName(), 
                user.getLastName(),
                user.getRole()
            ));
        } catch (AuthenticationException e) {
            logger.error("Kimlik doğrulama hatası: {}", e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Kullanıcı adı veya şifre hatalı",
                System.currentTimeMillis()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            logger.error("Giriş işlemi sırasında hata: {}", e.getMessage(), e);
            ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Giriş işlemi sırasında bir hata oluştu",
                System.currentTimeMillis()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/register")
    @Operation(
        summary = "Yeni kullanıcı kaydı",
        description = "Sisteme yeni bir hasta kaydı oluşturur",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Kullanıcı başarıyla kaydedildi",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AuthenticationResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Geçersiz kayıt bilgileri",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid RegisterRequest request) {
        logger.info("Kayıt isteği alındı - Request Body: {}", request);
        logger.info("Kayıt isteği alındı - Email: {}", request.getEmail());
        
        try {
            // Email kontrolü
            if (userRepository.existsByEmail(request.getEmail())) {
                logger.warn("Email zaten kullanımda: {}", request.getEmail());
                throw new BusinessException("Bu email adresi zaten kullanımda");
            }

            // Yeni kullanıcı oluştur
            var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.PATIENT)
                .isActive(true)
                .build();

            logger.debug("Yeni kullanıcı oluşturuldu: {}", user.getEmail());

            // Kullanıcıyı kaydet
            userRepository.save(user);
            logger.info("Kullanıcı başarıyla kaydedildi: {}", user.getEmail());

            // JWT token oluştur
            String token = jwtService.generateToken(user);
            logger.debug("JWT token oluşturuldu");

            // Hoşgeldiniz maili gönder
            try {
                emailService.sendWelcomeEmail(user.getEmail());
                logger.info("Hoşgeldiniz maili gönderildi: {}", user.getEmail());
            } catch (Exception e) {
                // Mail hatası olsa bile kayıt işlemini başarılı sayıyoruz
                logger.error("Mail gönderimi sırasında hata: {}", e.getMessage());
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthenticationResponse(token));
                
        } catch (BusinessException e) {
            logger.error("İş kuralı hatası oluştu: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Kayıt işlemi sırasında hata: {}", e.getMessage());
            logger.error("Hata detayı:", e);
            throw e;
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {
        try {
            AuthenticationResponse response = authenticationService.authenticateWithGoogle(request.getCredential());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Google login işlemi başarısız: " + e.getMessage(),
                    System.currentTimeMillis()
                ));
        }
    }

    @PostMapping("/send-welcome-email")
    public ResponseEntity<String> sendWelcomeEmail(@RequestBody EmailRequest request) {
        try {
            emailService.sendWelcomeEmail(request.getEmail());
            return ResponseEntity.ok("Hoşgeldiniz maili başarıyla gönderildi");
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Mail gönderimi sırasında bir hata oluştu: " + e.getMessage());
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class GoogleLoginRequest {
        private String credential;
        
        public String getCredential() {
            return credential;
        }
        
        public void setCredential(String credential) {
            this.credential = credential;
        }
    }
} 