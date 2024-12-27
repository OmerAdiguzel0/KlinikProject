package com.psychiatryclinic.api.controllers;

import com.psychiatryclinic.business.abstracts.AppointmentService;
import com.psychiatryclinic.business.requests.appointment.CreateAppointmentRequest;
import com.psychiatryclinic.business.requests.appointment.UpdateAppointmentRequest;
import com.psychiatryclinic.business.responses.appointment.GetAllAppointmentsResponse;
import com.psychiatryclinic.business.responses.appointment.GetAppointmentResponse;
import com.psychiatryclinic.entities.enums.AppointmentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@AllArgsConstructor
@CrossOrigin
@Tag(name = "Randevular", description = "Randevu yönetimi endpointleri")
@SecurityRequirement(name = "bearerAuth")
public class AppointmentController {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    @Operation(summary = "Yeni randevu oluştur", description = "Admin veya hasta yeni randevu oluşturabilir")
    public ResponseEntity<Void> add(@Valid @RequestBody CreateAppointmentRequest createAppointmentRequest) {
        logger.info("Yeni randevu oluşturma isteği alındı");
        appointmentService.add(createAppointmentRequest);
        logger.info("Randevu başarıyla oluşturuldu");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isAppointmentOwner(#updateAppointmentRequest.id)")
    @Operation(summary = "Randevu güncelle", description = "Admin veya randevunun sahibi güncelleyebilir")
    public ResponseEntity<Void> update(@Valid @RequestBody UpdateAppointmentRequest updateAppointmentRequest) {
        logger.info("Randevu güncelleme isteği alındı. ID: {}", updateAppointmentRequest.getId());
        appointmentService.update(updateAppointmentRequest);
        logger.info("Randevu başarıyla güncellendi. ID: {}", updateAppointmentRequest.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isAppointmentOwner(#id)")
    @Operation(summary = "Randevu sil", description = "Admin veya randevunun sahibi silebilir")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        logger.info("Randevu silme isteği alındı. ID: {}", id);
        appointmentService.delete(id);
        logger.info("Randevu başarıyla silindi. ID: {}", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isAppointmentOwner(#id) or @userSecurity.isDoctorOfAppointment(#id)")
    @Operation(summary = "Randevu getir", description = "Admin, randevunun sahibi veya doktoru görüntüleyebilir")
    public ResponseEntity<GetAppointmentResponse> getById(@PathVariable String id) {
        logger.info("Randevu bilgisi isteği alındı. ID: {}", id);
        return new ResponseEntity<>(appointmentService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isPatientOwner(#patientId)")
    @Operation(summary = "Hasta randevularını getir", description = "Admin veya hasta kendi randevularını görüntüleyebilir")
    public ResponseEntity<List<GetAppointmentResponse>> getByPatientId(@PathVariable String patientId) {
        logger.info("Hasta randevuları isteği alındı. Hasta ID: {}", patientId);
        return new ResponseEntity<>(appointmentService.getByPatientId(patientId), HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isDoctorOwner(#doctorId)")
    @Operation(summary = "Doktor randevularını getir", description = "Admin veya doktor kendi randevularını görüntüleyebilir")
    public ResponseEntity<List<GetAppointmentResponse>> getByDoctorId(@PathVariable String doctorId) {
        logger.info("Doktor randevuları isteği alındı. Doktor ID: {}", doctorId);
        return new ResponseEntity<>(appointmentService.getByDoctorId(doctorId), HttpStatus.OK);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tarih aralığındaki randevuları getir", description = "Sadece admin tarih aralığındaki tüm randevuları görüntüleyebilir")
    public ResponseEntity<List<GetAppointmentResponse>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        logger.info("Tarih aralığı randevuları isteği alındı. Başlangıç: {}, Bitiş: {}", start, end);
        return new ResponseEntity<>(appointmentService.getByDateRange(start, end), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tüm randevuları listele", description = "Sadece admin tüm randevuları listeleyebilir")
    public ResponseEntity<List<GetAllAppointmentsResponse>> getAll() {
        logger.info("Tüm randevular listesi isteği alındı");
        return new ResponseEntity<>(appointmentService.getAll(), HttpStatus.OK);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isDoctorOfAppointment(#id)")
    @Operation(summary = "Randevu durumunu güncelle", description = "Admin veya randevunun doktoru durumu güncelleyebilir")
    public ResponseEntity<Void> updateStatus(
        @PathVariable String id,
        @RequestParam AppointmentStatus status) {
        logger.info("Randevu durumu güncelleme isteği alındı. ID: {}, Yeni Durum: {}", id, status);
        appointmentService.updateStatus(id, status);
        logger.info("Randevu durumu güncellendi. ID: {}", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
} 