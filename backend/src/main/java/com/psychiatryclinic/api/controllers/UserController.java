package com.psychiatryclinic.api.controllers;

import com.psychiatryclinic.business.abstracts.UserService;
import com.psychiatryclinic.business.requests.user.CreateUserRequest;
import com.psychiatryclinic.business.requests.user.UpdateUserRequest;
import com.psychiatryclinic.business.responses.user.GetAllUsersResponse;
import com.psychiatryclinic.business.responses.user.GetUserResponse;
import com.psychiatryclinic.core.exceptions.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import com.psychiatryclinic.entities.enums.Role;
import com.psychiatryclinic.business.concretes.UserManager;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
@CrossOrigin
@Tag(name = "Kullanıcı Yönetimi", description = "Kullanıcı CRUD işlemleri ve yönetimi")
@Validated
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private UserService userService;
    private UserManager userManager;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Yeni kullanıcı ekle",
        description = "Sisteme yeni bir kullanıcı kaydı oluşturur. Sadece admin kullanabilir.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Kullanıcı başarıyla oluşturuldu",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Geçersiz istek",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        value = """
                            {
                                "status": 400,
                                "message": "Email adresi zaten kullanımda",
                                "timestamp": 1703127600000
                            }
                            """
                    )
                )
            )
        }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CreateUserRequest.class),
            examples = @ExampleObject(
                value = """
                    {
                        "firstName": "John",
                        "lastName": "Doe",
                        "email": "john.doe@example.com",
                        "password": "password123",
                        "phoneNumber": "+905551234567",
                        "role": "PATIENT"
                    }
                    """
            )
        )
    )
    public ResponseEntity<?> add(@Valid @RequestBody CreateUserRequest createUserRequest) {
        logger.info("Yeni kullanıcı kaydı başlıyor: {}", createUserRequest.getEmail());
        try {
            userService.add(createUserRequest);
            logger.info("Kullanıcı kaydı başarılı: {}", createUserRequest.getEmail());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Kullanıcı kaydı sırasında hata: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwner(#updateUserRequest.id)")
    @Operation(summary = "Kullanıcı güncelle", description = "Admin veya kullanıcının kendisi güncelleyebilir")
    public ResponseEntity<Void> update(@RequestBody UpdateUserRequest updateUserRequest) {
        userService.update(updateUserRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Kullanıcı sil", description = "Sadece admin kullanıcı silebilir")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        logger.info("Kullanıcı silme isteği alındı. ID: {}", id);
        userService.delete(id);
        logger.info("Kullanıcı başarıyla silindi. ID: {}", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwner(#id)")
    @Operation(summary = "Kullanıcı getir", description = "Admin veya kullanıcının kendisi görüntüleyebilir")
    public ResponseEntity<GetUserResponse> getById(@PathVariable String id) {
        logger.info("Kullanıcı bilgisi isteği alındı. ID: {}", id);
        GetUserResponse response = userService.getById(id);
        logger.info("Kullanıcı bilgisi döndürüldü. ID: {}", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwnerByEmail(#email)")
    @Operation(summary = "Email ile kullanıcı getir", description = "Admin veya kullanıcının kendisi görüntüleyebilir")
    public ResponseEntity<GetUserResponse> getByEmail(@PathVariable String email) {
        return new ResponseEntity<>(userService.getByEmail(email), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tüm kullanıcıları listele", description = "Sadece admin tüm kullanıcıları listeleyebilir")
    public ResponseEntity<List<GetAllUsersResponse>> getAll() {
        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }

    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUserRole(
            @PathVariable String userId,
            @RequestParam Role newRole) {
        userManager.updateUserRole(userId, newRole);
        return ResponseEntity.ok("Kullanıcı rolü başarıyla güncellendi");
    }
} 