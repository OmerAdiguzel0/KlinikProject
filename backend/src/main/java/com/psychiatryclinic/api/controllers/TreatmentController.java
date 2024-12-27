package com.psychiatryclinic.api.controllers;

import com.psychiatryclinic.business.abstracts.TreatmentService;
import com.psychiatryclinic.business.requests.treatment.CreateTreatmentRequest;
import com.psychiatryclinic.business.requests.treatment.UpdateTreatmentRequest;
import com.psychiatryclinic.business.responses.treatment.GetAllTreatmentsResponse;
import com.psychiatryclinic.business.responses.treatment.GetTreatmentResponse;
import com.psychiatryclinic.entities.enums.TreatmentStatus;
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
@RequestMapping("/api/v1/treatments")
@AllArgsConstructor
@CrossOrigin
@Tag(name = "Tedaviler", description = "Tedavi yönetimi endpointleri")
@SecurityRequirement(name = "bearerAuth")
public class TreatmentController {
    private TreatmentService treatmentService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(
        summary = "Yeni tedavi ekle",
        description = "Doktor rolüne sahip kullanıcılar yeni tedavi kaydı oluşturabilir",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Tedavi başarıyla oluşturuldu",
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
            schema = @Schema(implementation = CreateTreatmentRequest.class),
            examples = @ExampleObject(
                value = """
                    {
                        "patientId": "123e4567-e89b-12d3-a456-426614174000",
                        "doctorId": "123e4567-e89b-12d3-a456-426614174001",
                        "diagnosis": "Anksiyete Bozukluğu",
                        "treatmentPlan": "Haftalık terapi seansları ve ilaç tedavisi",
                        "medications": [
                            {
                                "name": "Prozac",
                                "dosage": "20mg",
                                "frequency": "Günde 1 kez",
                                "duration": "3 ay",
                                "instructions": "Sabah aç karnına alınmalı"
                            }
                        ],
                        "notes": "Hasta ilerleme gösteriyor"
                    }
                    """
            )
        )
    )
    public ResponseEntity<Void> add(@RequestBody CreateTreatmentRequest createTreatmentRequest) {
        treatmentService.add(createTreatmentRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(
        summary = "Tedavi güncelle",
        description = "Doktor rolüne sahip kullanıcılar tedavi bilgilerini güncelleyebilir",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Tedavi başarıyla güncellendi"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Tedavi bulunamadı",
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
            schema = @Schema(implementation = UpdateTreatmentRequest.class),
            examples = @ExampleObject(
                value = """
                    {
                        "id": "123e4567-e89b-12d3-a456-426614174000",
                        "diagnosis": "Anksiyete Bozukluğu - İyileşme Görülüyor",
                        "treatmentPlan": "Terapi seansları iki haftada bire düşürülecek",
                        "notes": "Hasta önemli ilerleme gösteriyor",
                        "status": "ACTIVE"
                    }
                    """
            )
        )
    )
    public ResponseEntity<Void> update(@RequestBody UpdateTreatmentRequest updateTreatmentRequest) {
        treatmentService.update(updateTreatmentRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(
        summary = "Tedavi sil",
        description = "Doktor rolüne sahip kullanıcılar tedavi kaydını silebilir",
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "Tedavi başarıyla silindi"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Tedavi bulunamadı",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<Void> delete(@PathVariable String id) {
        treatmentService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(
        summary = "Tedavi durumunu güncelle",
        description = "Doktor rolüne sahip kullanıcılar tedavi durumunu güncelleyebilir",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Tedavi durumu başarıyla güncellendi"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Tedavi bulunamadı",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<Void> updateStatus(@PathVariable String id, @RequestParam TreatmentStatus status) {
        treatmentService.updateStatus(id, status);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    @Operation(
        summary = "Tedavi detaylarını getir",
        description = "Doktor ve hasta rolüne sahip kullanıcılar tedavi detaylarını görüntüleyebilir",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Tedavi detayları başarıyla getirildi",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetTreatmentResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Tedavi bulunamadı",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<GetTreatmentResponse> getById(@PathVariable String id) {
        return new ResponseEntity<>(treatmentService.getById(id), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(
        summary = "Tüm tedavileri listele",
        description = "Doktor rolüne sahip kullanıcılar tüm tedavileri listeleyebilir",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Tedaviler başarıyla listelendi",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetAllTreatmentsResponse.class)
                )
            )
        }
    )
    public ResponseEntity<List<GetAllTreatmentsResponse>> getAll() {
        return new ResponseEntity<>(treatmentService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    @Operation(
        summary = "Hastanın tedavilerini listele",
        description = "Doktor ve hasta rolüne sahip kullanıcılar hastanın tedavilerini görüntüleyebilir",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Tedaviler başarıyla listelendi",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetTreatmentResponse.class)
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
    public ResponseEntity<List<GetTreatmentResponse>> getByPatientId(@PathVariable String patientId) {
        return new ResponseEntity<>(treatmentService.getByPatientId(patientId), HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(
        summary = "Doktorun tedavilerini listele",
        description = "Doktor rolüne sahip kullanıcılar kendi tedavilerini listeleyebilir",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Tedaviler başarıyla listelendi",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetTreatmentResponse.class)
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
    public ResponseEntity<List<GetTreatmentResponse>> getByDoctorId(@PathVariable String doctorId) {
        return new ResponseEntity<>(treatmentService.getByDoctorId(doctorId), HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(
        summary = "Duruma göre tedavileri listele",
        description = "Doktor rolüne sahip kullanıcılar belirli durumdaki tedavileri listeleyebilir",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Tedaviler başarıyla listelendi",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetTreatmentResponse.class)
                )
            )
        }
    )
    public ResponseEntity<List<GetTreatmentResponse>> getByStatus(@PathVariable TreatmentStatus status) {
        return new ResponseEntity<>(treatmentService.getByStatus(status), HttpStatus.OK);
    }
} 