package com.psychiatryclinic.business.concretes;

import com.psychiatryclinic.business.abstracts.InvoiceService;
import com.psychiatryclinic.business.requests.invoice.CreateInvoiceRequest;
import com.psychiatryclinic.business.responses.invoice.GetAllInvoicesResponse;
import com.psychiatryclinic.business.responses.invoice.GetInvoiceResponse;
import com.psychiatryclinic.core.exceptions.BusinessException;
import com.psychiatryclinic.core.utilities.invoice.InvoiceGenerator;
import com.psychiatryclinic.core.utilities.mappers.ModelMapperService;
import com.psychiatryclinic.dataAccess.InvoiceRepository;
import com.psychiatryclinic.entities.Invoice;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InvoiceManager implements InvoiceService {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceManager.class);
    
    private InvoiceRepository invoiceRepository;
    private ModelMapperService modelMapperService;
    private InvoiceGenerator invoiceGenerator;

    @Override
    public void add(CreateInvoiceRequest createInvoiceRequest) {
        logger.debug("Fatura oluşturma isteği alındı: {}", createInvoiceRequest);
        
        Invoice invoice = this.modelMapperService.forRequest()
                .map(createInvoiceRequest, Invoice.class);
        
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setPaid(false);
        
        // PDF oluştur ve kaydet
        byte[] pdfContent = invoiceGenerator.generatePdf(invoice);
        invoice.setPdfContent(pdfContent);
        
        invoiceRepository.save(invoice);
        logger.info("Yeni fatura oluşturuldu. ID: {}", invoice.getId());
    }

    @Override
    public GetInvoiceResponse getById(String id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Fatura bulunamadı"));
        
        return modelMapperService.forResponse()
                .map(invoice, GetInvoiceResponse.class);
    }

    @Override
    public List<GetAllInvoicesResponse> getAll() {
        List<Invoice> invoices = invoiceRepository.findAll();
        
        return invoices.stream()
                .map(invoice -> modelMapperService.forResponse()
                        .map(invoice, GetAllInvoicesResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<GetInvoiceResponse> getByPatientId(String patientId) {
        List<Invoice> invoices = invoiceRepository.findByPatientId(patientId);
        
        return invoices.stream()
                .map(invoice -> modelMapperService.forResponse()
                        .map(invoice, GetInvoiceResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public byte[] generatePdf(String id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Fatura bulunamadı"));
        
        if (invoice.getPdfContent() == null) {
            invoice.setPdfContent(invoiceGenerator.generatePdf(invoice));
            invoiceRepository.save(invoice);
        }
        
        return invoice.getPdfContent();
    }

    @Override
    public boolean verifyInvoice(String id) {
        return invoiceRepository.existsById(id);
    }
} 