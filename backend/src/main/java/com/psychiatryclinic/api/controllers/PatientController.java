package com.psychiatryclinic.api.controllers;

import com.psychiatryclinic.business.abstracts.PatientService;
import com.psychiatryclinic.business.requests.patient.CreatePatientRequest;
import com.psychiatryclinic.business.requests.patient.UpdatePatientRequest;
import com.psychiatryclinic.business.responses.patient.GetAllPatientsResponse;
import com.psychiatryclinic.business.responses.patient.GetPatientResponse;
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
@RequestMapping("/api/v1/patients")
@AllArgsConstructor
@CrossOrigin
@Tag(name = "Hasta Yönetimi", description = "Hasta CRUD işlemleri ve yönetimi")
@SecurityRequirement(name = "bearerAuth")
public class PatientController {
    private PatientService patientService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Yeni hasta ekle",
        description = "Sisteme yeni bir hasta kaydı oluşturur. Sadece admin kullanabilir.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Hasta başarıyla oluşturuldu",
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
            schema = @Schema(implementation = CreatePatientRequest.class),
            examples = @ExampleObject(
                value = """
                    {
                        "userId": "123e4567-e89b-12d3-a456-426614174000",
                        "dateOfBirth": "1990-01-01",
                        "gender": "ERKEK",
                        "address": "İstanbul, Türkiye",
                        "emergencyContact": "Jane Doe",
                        "emergencyContactPhone": "+905551234567",
                        "bloodType": "A_POSITIVE"
                    }
                    """
            )
        )
    )
    public ResponseEntity<Void> add(@RequestBody CreatePatientRequest createPatientRequest) {
        patientService.add(createPatientRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isPatientOwner(#updatePatientRequest.id)")
    @Operation(
        summary = "Hasta bilgilerini güncelle",
        description = "Var olan bir hastanın bilgilerini günceller. Admin veya hastanın kendisi güncelleyebilir.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Hasta bilgileri başarıyla güncellendi"
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
            schema = @Schema(implementation = UpdatePatientRequest.class),
            examples = @ExampleObject(
                value = """
                    {
                        "id": "123e4567-e89b-12d3-a456-426614174000",
                        "address": "Ankara, Türkiye",
                        "emergencyContact": "John Doe",
                        "emergencyContactPhone": "+905559876543"
                    }
                    """
            )
        )
    )
    public ResponseEntity<Void> update(@RequestBody UpdatePatientRequest updatePatientRequest) {
        patientService.update(updatePatientRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Hasta sil",
        description = "Belirtilen ID'ye sahip hastayı sistemden siler. Sadece admin kullanabilir.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Hasta başarıyla silindi"
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
                description = "Hasta bulunamadı",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<Void> delete(@PathVariable String id) {
        patientService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isPatientOwner(#id) or @userSecurity.isDoctorOfPatient(#id)")
    @Operation(summary = "Hasta getir", description = "Admin, hastanın kendisi veya doktoru görüntüleyebilir")
    public ResponseEntity<GetPatientResponse> getById(@PathVariable String id) {
        return new ResponseEntity<>(patientService.getById(id), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tüm hastaları listele", description = "Sadece admin tüm hastaları listeleyebilir")
    public ResponseEntity<List<GetAllPatientsResponse>> getAll() {
        return new ResponseEntity<>(patientService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isDoctorOwner(#doctorId)")
    @Operation(summary = "Doktora göre hastaları getir", description = "Admin veya ilgili doktor kendi hastalarını görüntüleyebilir")
    public ResponseEntity<List<GetPatientResponse>> getByDoctorId(@PathVariable String doctorId) {
        return new ResponseEntity<>(patientService.getByDoctorId(doctorId), HttpStatus.OK);
    }
} 