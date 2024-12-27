package com.psychiatryclinic.api.controllers;

import com.psychiatryclinic.business.abstracts.DoctorService;
import com.psychiatryclinic.business.requests.doctor.CreateDoctorRequest;
import com.psychiatryclinic.business.requests.doctor.UpdateDoctorRequest;
import com.psychiatryclinic.business.responses.doctor.GetAllDoctorsResponse;
import com.psychiatryclinic.business.responses.doctor.GetDoctorResponse;
import com.psychiatryclinic.core.exceptions.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@AllArgsConstructor
@CrossOrigin
@Tag(name = "Doktor Yönetimi", description = "Doktor CRUD işlemleri ve yönetimi")
@SecurityRequirement(name = "bearerAuth")
public class DoctorController {
    private DoctorService doctorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Yeni doktor ekle",
        description = "Sisteme yeni bir doktor kaydı oluşturur. Sadece admin kullanabilir.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Doktor başarıyla oluşturuldu",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Geçersiz istek",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CreateDoctorRequest.class),
            examples = @ExampleObject(
                value = """
                    {
                        "userId": "123e4567-e89b-12d3-a456-426614174000",
                        "specialization": "PSİKİYATRİ",
                        "title": "PROF_DR",
                        "licenseNumber": "12345",
                        "biography": "20 yıllık deneyimli psikiyatrist"
                    }
                    """
            )
        )
    )
    public ResponseEntity<Void> add(@RequestBody CreateDoctorRequest createDoctorRequest) {
        doctorService.add(createDoctorRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isDoctorOwner(#updateDoctorRequest.id)")
    @Operation(
        summary = "Doktor bilgilerini güncelle",
        description = "Var olan bir doktorun bilgilerini günceller. Admin veya doktorun kendisi güncelleyebilir.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Doktor bilgileri başarıyla güncellendi"
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Yetkisiz erişim",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UpdateDoctorRequest.class),
            examples = @ExampleObject(
                value = """
                    {
                        "id": "123e4567-e89b-12d3-a456-426614174000",
                        "specialization": "PSİKİYATRİ",
                        "title": "PROF_DR",
                        "biography": "25 yıllık deneyimli psikiyatrist"
                    }
                    """
            )
        )
    )
    public ResponseEntity<Void> update(@RequestBody UpdateDoctorRequest updateDoctorRequest) {
        doctorService.update(updateDoctorRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Doktor sil",
        description = "Belirtilen ID'ye sahip doktoru sistemden siler. Sadece admin kullanabilir.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Doktor başarıyla silindi"
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Yetkisiz erişim",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Doktor bulunamadı",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<Void> delete(@PathVariable String id) {
        doctorService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    @Operation(
        summary = "Doktor getir",
        description = "Belirtilen ID'ye sahip doktorun detaylı bilgilerini getirir.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Doktor bilgileri başarıyla getirildi",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetDoctorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Doktor bulunamadı",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<GetDoctorResponse> getById(@PathVariable String id) {
        return new ResponseEntity<>(doctorService.getById(id), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    @Operation(
        summary = "Tüm doktorları listele",
        description = "Sistemdeki tüm doktorların listesini getirir. Admin ve hastalar görüntüleyebilir.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Doktor listesi başarıyla getirildi",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetAllDoctorsResponse.class),
                    examples = @ExampleObject(
                        value = """
                            [
                                {
                                    "id": "123e4567-e89b-12d3-a456-426614174000",
                                    "fullName": "Dr. John Doe",
                                    "specialization": "PSİKİYATRİ",
                                    "title": "PROF_DR",
                                    "isAvailable": true
                                }
                            ]
                            """
                    )
                )
            )
        }
    )
    public ResponseEntity<List<GetAllDoctorsResponse>> getAll() {
        return new ResponseEntity<>(doctorService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/specialization/{specialization}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    @Operation(
        summary = "Uzmanlık alanına göre doktorları getir",
        description = "Belirtilen uzmanlık alanındaki tüm doktorları listeler. Admin ve hastalar görüntüleyebilir.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Doktor listesi başarıyla getirildi",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetDoctorResponse.class),
                    examples = @ExampleObject(
                        value = """
                            [
                                {
                                    "id": "123e4567-e89b-12d3-a456-426614174000",
                                    "user": {
                                        "firstName": "John",
                                        "lastName": "Doe",
                                        "email": "john.doe@example.com"
                                    },
                                    "specialization": "PSİKİYATRİ",
                                    "title": "PROF_DR",
                                    "biography": "20 yıllık deneyimli psikiyatrist",
                                    "isAvailable": true
                                }
                            ]
                            """
                    )
                )
            )
        }
    )
    public ResponseEntity<List<GetDoctorResponse>> getBySpecialization(@PathVariable String specialization) {
        return new ResponseEntity<>(doctorService.getBySpecialization(specialization), HttpStatus.OK);
    }

    @GetMapping("/public/all")
    @Operation(
        summary = "Tüm doktorları listele (Public)",
        description = "Giriş yapmadan tüm doktorların listesini getirir",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Doktor listesi başarıyla getirildi",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetAllDoctorsResponse.class)
                )
            )
        }
    )
    public ResponseEntity<List<GetAllDoctorsResponse>> getAllPublic() {
        return new ResponseEntity<>(doctorService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/public/{id}")
    @Operation(
        summary = "Doktor detaylarını getir (Public)",
        description = "Giriş yapmadan belirli bir doktorun detaylarını getirir",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Doktor detayları başarıyla getirildi",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetDoctorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Doktor bulunamadı",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<GetDoctorResponse> getByIdPublic(@PathVariable String id) {
        return new ResponseEntity<>(doctorService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/public/specialization/{specialization}")
    @Operation(
        summary = "Uzmanlık alanına göre doktorları getir (Public)",
        description = "Giriş yapmadan belirli bir uzmanlık alanındaki doktorları listeler",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Doktor listesi başarıyla getirildi",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetDoctorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<List<GetDoctorResponse>> getBySpecializationPublic(@PathVariable String specialization) {
        return new ResponseEntity<>(doctorService.getBySpecialization(specialization), HttpStatus.OK);
    }
} 