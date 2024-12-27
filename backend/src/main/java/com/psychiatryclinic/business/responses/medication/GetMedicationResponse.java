package com.psychiatryclinic.business.responses.medication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetMedicationResponse {
    private String id;
    private String patientId;
    private String doctorId;
    private String medicationName;
    private String dosage;
    private String frequency;
    private String instructions;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;
} 