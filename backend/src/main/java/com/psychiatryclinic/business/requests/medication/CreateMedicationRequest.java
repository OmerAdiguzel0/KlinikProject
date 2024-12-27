package com.psychiatryclinic.business.requests.medication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMedicationRequest {
    private String patientId;
    private String doctorId;
    private String medicationName;
    private String dosage;
    private String frequency;
    private String instructions;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
} 