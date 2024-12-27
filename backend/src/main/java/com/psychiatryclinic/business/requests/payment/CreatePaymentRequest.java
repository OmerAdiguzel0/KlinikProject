package com.psychiatryclinic.business.requests.payment;

import com.psychiatryclinic.entities.enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    @NotNull(message = "Randevu ID boş olamaz")
    private String appointmentId;
    
    @NotNull(message = "Ödeme tutarı boş olamaz")
    @Positive(message = "Ödeme tutarı pozitif olmalıdır")
    private BigDecimal amount;
    
    @NotNull(message = "Ödeme tipi boş olamaz")
    private PaymentType type;
    
    private String notes;
} 