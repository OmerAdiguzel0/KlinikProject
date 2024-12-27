package com.psychiatryclinic.business.abstracts;

import com.psychiatryclinic.business.requests.invoice.CreateInvoiceRequest;
import com.psychiatryclinic.business.responses.invoice.GetAllInvoicesResponse;
import com.psychiatryclinic.business.responses.invoice.GetInvoiceResponse;

import java.util.List;

public interface InvoiceService {
    void add(CreateInvoiceRequest createInvoiceRequest);
    GetInvoiceResponse getById(String id);
    List<GetAllInvoicesResponse> getAll();
    List<GetInvoiceResponse> getByPatientId(String patientId);
    byte[] generatePdf(String id);
    boolean verifyInvoice(String id);
} 