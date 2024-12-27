package com.psychiatryclinic.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "invoices")
public class Invoice {
    @Id
    private String id;
    private String patientId;
    private String appointmentId;
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private boolean isPaid;
    private byte[] pdfContent;
} 