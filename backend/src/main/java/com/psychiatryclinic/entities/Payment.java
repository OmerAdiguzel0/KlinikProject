package com.psychiatryclinic.entities;

import com.psychiatryclinic.entities.enums.PaymentStatus;
import com.psychiatryclinic.entities.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;
    private String patientId;
    private String doctorId;
    
    @DBRef
    private Appointment appointment;
    
    private BigDecimal amount;
    private PaymentType type;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private String transactionId;
    private String notes;
    private String invoiceId;
} 