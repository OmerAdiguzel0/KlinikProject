package com.psychiatryclinic.api.controllers;

import com.psychiatryclinic.business.abstracts.WorkingHoursService;
import com.psychiatryclinic.business.requests.workinghours.CreateWorkingHoursRequest;
import com.psychiatryclinic.business.responses.workinghours.GetWorkingHoursResponse;
import com.psychiatryclinic.entities.enums.DayOfWeek;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/v1/working-hours")
@AllArgsConstructor
@CrossOrigin
@Tag(name = "Çalışma Saatleri", description = "Doktor çalışma saatleri yönetimi endpointleri")
@SecurityRequirement(name = "bearerAuth")
public class WorkingHoursController {
    private static final Logger logger = LoggerFactory.getLogger(WorkingHoursController.class);
    private final WorkingHoursService workingHoursService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isDoctorOwner(#createWorkingHoursRequest.doctorId)")
    @Operation(summary = "Çalışma saati ekle", description = "Admin veya doktor kendi çalışma saatlerini ekleyebilir")
    public ResponseEntity<Void> add(@Valid @RequestBody CreateWorkingHoursRequest createWorkingHoursRequest) {
        logger.info("Yeni çalışma saati ekleme isteği alındı. Doktor ID: {}", createWorkingHoursRequest.getDoctorId());
        workingHoursService.add(createWorkingHoursRequest);
        logger.info("Çalışma saati başarıyla eklendi. Doktor ID: {}", createWorkingHoursRequest.getDoctorId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isWorkingHoursOwner(#id)")
    @Operation(summary = "Çalışma saati sil", description = "Admin veya doktor kendi çalışma saatlerini silebilir")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        logger.info("Çalışma saati silme isteği alındı. ID: {}", id);
        workingHoursService.delete(id);
        logger.info("Çalışma saati başarıyla silindi. ID: {}", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    @Operation(summary = "Çalışma saati getir", description = "Tüm roller çalışma saatlerini görüntüleyebilir")
    public ResponseEntity<GetWorkingHoursResponse> getById(@PathVariable String id) {
        logger.info("Çalışma saati bilgisi isteği alındı. ID: {}", id);
        return new ResponseEntity<>(workingHoursService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    @Operation(summary = "Doktor çalışma saatlerini getir", description = "Tüm roller doktor çalışma saatlerini görüntüleyebilir")
    public ResponseEntity<List<GetWorkingHoursResponse>> getByDoctorId(@PathVariable String doctorId) {
        logger.info("Doktor çalışma saatleri isteği alındı. Doktor ID: {}", doctorId);
        return new ResponseEntity<>(workingHoursService.getByDoctorId(doctorId), HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorId}/day/{dayOfWeek}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    @Operation(summary = "Doktorun belirli gün çalışma saatlerini getir", description = "Tüm roller doktorun belirli gündeki çalışma saatlerini görüntüleyebilir")
    public ResponseEntity<List<GetWorkingHoursResponse>> getByDoctorIdAndDay(
            @PathVariable String doctorId,
            @PathVariable DayOfWeek dayOfWeek) {
        logger.info("Doktor günlük çalışma saatleri isteği alındı. Doktor ID: {}, Gün: {}", doctorId, dayOfWeek);
        return new ResponseEntity<>(workingHoursService.getByDoctorIdAndDay(doctorId, dayOfWeek), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tüm çalışma saatlerini listele", description = "Sadece admin tüm çalışma saatlerini listeleyebilir")
    public ResponseEntity<List<GetWorkingHoursResponse>> getAll() {
        logger.info("Tüm çalışma saatleri listesi isteği alındı");
        return new ResponseEntity<>(workingHoursService.getAll(), HttpStatus.OK);
    }
} 