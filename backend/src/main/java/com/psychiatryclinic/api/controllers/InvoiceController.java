package com.psychiatryclinic.api.controllers;

import com.psychiatryclinic.business.abstracts.InvoiceService;
import com.psychiatryclinic.business.requests.invoice.CreateInvoiceRequest;
import com.psychiatryclinic.business.responses.invoice.GetAllInvoicesResponse;
import com.psychiatryclinic.business.responses.invoice.GetInvoiceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
@AllArgsConstructor
@Tag(name = "Fatura İşlemleri", description = "Fatura yönetimi için API endpoint'leri")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin
public class InvoiceController {
    private InvoiceService invoiceService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @Operation(summary = "Yeni fatura oluştur", description = "Yeni bir fatura oluşturur")
    public ResponseEntity<Void> add(@Valid @RequestBody CreateInvoiceRequest createInvoiceRequest) {
        invoiceService.add(createInvoiceRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or @userSecurity.isOwner(#id)")
    @Operation(summary = "Fatura detaylarını getir", description = "Belirtilen ID'ye sahip faturanın detaylarını getirir")
    public ResponseEntity<GetInvoiceResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(invoiceService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tüm faturaları listele", description = "Sistemdeki tüm faturaları listeler")
    public ResponseEntity<List<GetAllInvoicesResponse>> getAll() {
        return ResponseEntity.ok(invoiceService.getAll());
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or @userSecurity.isOwner(#patientId)")
    @Operation(summary = "Hasta faturalarını listele", description = "Belirtilen hastaya ait faturaları listeler")
    public ResponseEntity<List<GetInvoiceResponse>> getByPatientId(@PathVariable String patientId) {
        return ResponseEntity.ok(invoiceService.getByPatientId(patientId));
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR') or @userSecurity.isOwner(#id)")
    @Operation(summary = "Fatura PDF'ini indir", description = "Belirtilen faturanın PDF versiyonunu indirir")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable String id) {
        byte[] pdfContent = invoiceService.generatePdf(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "fatura-" + id + ".pdf");
        
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

    @GetMapping("/{id}/verify")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Fatura doğrula", description = "Admin veya doktor fatura doğrulaması yapabilir")
    public ResponseEntity<Boolean> verifyInvoice(@PathVariable String id) {
        return ResponseEntity.ok(invoiceService.verifyInvoice(id));
    }
} 