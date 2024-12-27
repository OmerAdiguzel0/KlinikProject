package com.psychiatryclinic.business.requests.treatment;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateTreatmentRequest {
    private String patientId;
    private String doctorId;
    private String diagnosis;
    private String treatmentPlan;
    private List<MedicationRequest> medications;
    private String notes;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Data
    public static class MedicationRequest {
        private String name;
        private String dosage;
        private String frequency;
        private String duration;
        private String instructions;
    }
} 