package com.psychiatryclinic.api.controllers;

import com.psychiatryclinic.business.abstracts.MedicationService;
import com.psychiatryclinic.business.requests.medication.CreateMedicationRequest;
import com.psychiatryclinic.business.responses.medication.GetAllMedicationsResponse;
import com.psychiatryclinic.business.responses.medication.GetMedicationResponse;
import com.psychiatryclinic.core.exceptions.ErrorResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medications")
@AllArgsConstructor
@CrossOrigin
@Tag(name = "İlaçlar", description = "İlaç takibi endpointleri")
@SecurityRequirement(name = "bearerAuth")
public class MedicationController {
    private MedicationService medicationService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(
        summary = "Yeni ilaç ekle",
        description = "Doktor rolüne sahip kullanıcılar yeni ilaç kaydı oluşturabilir",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "İlaç başarıyla oluşturuldu",
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
            schema = @Schema(implementation = CreateMedicationRequest.class),
            examples = @ExampleObject(
                value = """
                    {
                        "patientId": "123e4567-e89b-12d3-a456-426614174000",
                        "doctorId": "123e4567-e89b-12d3-a456-426614174001",
                        "medicationName": "Prozac",
                        "dosage": "20mg",
                        "frequency": "Günde 1 kez",
                        "instructions": "Sabah aç karnına alınmalı",
                        "startDate": "2024-01-01T09:00:00",
                        "endDate": "2024-04-01T09:00:00"
                    }
                    """
            )
        )
    )
    public ResponseEntity<Void> add(@RequestBody CreateMedicationRequest createMedicationRequest) {
        medicationService.add(createMedicationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(
        summary = "İlaç kaydını sil",
        description = "Doktor rolüne sahip kullanıcılar ilaç kaydını silebilir",
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "İlaç kaydı başarıyla silindi"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "İlaç kaydı bulunamadı",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<Void> delete(@PathVariable String id) {
        medicationService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(
        summary = "İlaç durumunu güncelle",
        description = "Doktor rolüne sahip kullanıcılar ilaç durumunu aktif/pasif yapabilir",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "İlaç durumu başarıyla güncellendi"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "İlaç kaydı bulunamadı",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<Void> updateStatus(@PathVariable String id, @RequestParam boolean isActive) {
        medicationService.updateStatus(id, isActive);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    @Operation(
        summary = "İlaç detaylarını getir",
        description = "Doktor ve hasta rolüne sahip kullanıcılar ilaç detaylarını görüntüleyebilir",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "İlaç detayları başarıyla getirildi",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetMedicationResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "İlaç kaydı bulunamadı",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<GetMedicationResponse> getById(@PathVariable String id) {
        return new ResponseEntity<>(medicationService.getById(id), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(
        summary = "Tüm ilaçları listele",
        description = "Doktor rolüne sahip kullanıcılar tüm ilaç kayıtlarını listeleyebilir",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "İlaç listesi başarıyla getirildi",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetAllMedicationsResponse.class)
                )
            )
        }
    )
    public ResponseEntity<List<GetAllMedicationsResponse>> getAll() {
        return new ResponseEntity<>(medicationService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    @Operation(
        summary = "Hastanın ilaçlarını listele",
        description = "Doktor ve hasta rolüne sahip kullanıcılar hastanın ilaçlarını görüntüleyebilir",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Hasta ilaçları başarıyla listelendi",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetMedicationResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Hasta bulunamadı",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<List<GetMedicationResponse>> getByPatientId(@PathVariable String patientId) {
        return new ResponseEntity<>(medicationService.getByPatientId(patientId), HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(
        summary = "Doktorun yazdığı ilaçları listele",
        description = "Doktor rolüne sahip kullanıcılar kendi yazdıkları ilaçları listeleyebilir",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Doktor ilaçları başarıyla listelendi",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetMedicationResponse.class)
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
    public ResponseEntity<List<GetMedicationResponse>> getByDoctorId(@PathVariable String doctorId) {
        return new ResponseEntity<>(medicationService.getByDoctorId(doctorId), HttpStatus.OK);
    }

    @GetMapping("/patient/{patientId}/active")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    @Operation(
        summary = "Hastanın aktif ilaçlarını listele",
        description = "Doktor ve hasta rolüne sahip kullanıcılar hastanın aktif ilaçlarını görüntüleyebilir",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Aktif ilaçlar başarıyla listelendi",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetMedicationResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Hasta bulunamadı",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<List<GetMedicationResponse>> getActiveByPatientId(@PathVariable String patientId) {
        return new ResponseEntity<>(medicationService.getActiveByPatientId(patientId), HttpStatus.OK);
    }
} 