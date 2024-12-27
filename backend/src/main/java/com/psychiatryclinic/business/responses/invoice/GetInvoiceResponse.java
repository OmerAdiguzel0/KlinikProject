package com.psychiatryclinic.business.responses.invoice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetInvoiceResponse {
    private String id;
    private String patientId;
    private String appointmentId;
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private boolean isPaid;
} 