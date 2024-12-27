package com.psychiatryclinic.api.controllers;

import com.psychiatryclinic.business.abstracts.PaymentService;
import com.psychiatryclinic.business.requests.payment.CreatePaymentRequest;
import com.psychiatryclinic.business.responses.payment.GetPaymentResponse;
import com.psychiatryclinic.entities.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/payments")
@AllArgsConstructor
@CrossOrigin
@Tag(name = "Ödemeler", description = "Ödeme işlemleri endpointleri")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isPatientOwner(#createPaymentRequest.patientId)")
    @Operation(summary = "Yeni ödeme ekle", description = "Admin veya hasta yeni ödeme kaydı oluşturabilir")
    public ResponseEntity<Void> add(@Valid @RequestBody CreatePaymentRequest createPaymentRequest) {
        logger.info("Yeni ödeme ekleme isteği alındı. Randevu ID: {}", createPaymentRequest.getAppointmentId());
        paymentService.add(createPaymentRequest);
        logger.info("Ödeme başarıyla eklendi. Randevu ID: {}", createPaymentRequest.getAppointmentId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ödeme durumunu güncelle", description = "Sadece admin ödeme durumunu güncelleyebilir")
    public ResponseEntity<Void> updateStatus(
            @PathVariable String id,
            @RequestParam PaymentStatus status) {
        logger.info("Ödeme durumu güncelleme isteği alındı. ID: {}, Yeni Durum: {}", id, status);
        paymentService.updateStatus(id, status);
        logger.info("Ödeme durumu güncellendi. ID: {}", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isPaymentOwner(#id) or @userSecurity.isDoctorOfPayment(#id)")
    @Operation(summary = "Ödeme getir", description = "Admin, ödemenin sahibi veya doktoru görüntüleyebilir")
    public ResponseEntity<GetPaymentResponse> getById(@PathVariable String id) {
        logger.info("Ödeme bilgisi isteği alındı. ID: {}", id);
        return new ResponseEntity<>(paymentService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isAppointmentOwner(#appointmentId) or @userSecurity.isDoctorOfAppointment(#appointmentId)")
    @Operation(summary = "Randevu ödemelerini getir", description = "Admin, randevunun sahibi veya doktoru görüntüleyebilir")
    public ResponseEntity<List<GetPaymentResponse>> getByAppointmentId(@PathVariable String appointmentId) {
        logger.info("Randevu ödemeleri isteği alındı. Randevu ID: {}", appointmentId);
        return new ResponseEntity<>(paymentService.getByAppointmentId(appointmentId), HttpStatus.OK);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isPatientOwner(#patientId)")
    @Operation(summary = "Hasta ödemelerini getir", description = "Admin veya hasta kendi ödemelerini görüntüleyebilir")
    public ResponseEntity<List<GetPaymentResponse>> getByPatientId(@PathVariable String patientId) {
        logger.info("Hasta ödemeleri isteği alındı. Hasta ID: {}", patientId);
        return new ResponseEntity<>(paymentService.getByPatientId(patientId), HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isDoctorOwner(#doctorId)")
    @Operation(summary = "Doktor ödemelerini getir", description = "Admin veya doktor kendi ödemelerini görüntüleyebilir")
    public ResponseEntity<List<GetPaymentResponse>> getByDoctorId(@PathVariable String doctorId) {
        logger.info("Doktor ödemeleri isteği alındı. Doktor ID: {}", doctorId);
        return new ResponseEntity<>(paymentService.getByDoctorId(doctorId), HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Durum bazlı ödemeleri getir", description = "Sadece admin durum bazlı ödemeleri görüntüleyebilir")
    public ResponseEntity<List<GetPaymentResponse>> getByStatus(@PathVariable PaymentStatus status) {
        logger.info("Durum bazlı ödemeler isteği alındı. Durum: {}", status);
        return new ResponseEntity<>(paymentService.getByStatus(status), HttpStatus.OK);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tarih aralığındaki ödemeleri getir", description = "Sadece admin tarih aralığındaki ödemeleri görüntüleyebilir")
    public ResponseEntity<List<GetPaymentResponse>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        logger.info("Tarih aralığı ödemeleri isteği alındı. Başlangıç: {}, Bitiş: {}", start, end);
        return new ResponseEntity<>(paymentService.getByDateRange(start, end), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tüm ödemeleri listele", description = "Sadece admin tüm ödemeleri listeleyebilir")
    public ResponseEntity<List<GetPaymentResponse>> getAll() {
        logger.info("Tüm ödemeler listesi isteği alındı");
        return new ResponseEntity<>(paymentService.getAll(), HttpStatus.OK);
    }
} 