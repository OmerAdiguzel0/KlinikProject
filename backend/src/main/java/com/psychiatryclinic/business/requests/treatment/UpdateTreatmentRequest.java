package com.psychiatryclinic.business.requests.treatment;

import com.psychiatryclinic.entities.enums.TreatmentStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdateTreatmentRequest {
    private String id;
    private String diagnosis;
    private String treatmentPlan;
    private List<MedicationRequest> medications;
    private String notes;
    private LocalDateTime endDate;
    private TreatmentStatus status;

    @Data
    public static class MedicationRequest {
        private String name;
        private String dosage;
        private String frequency;
        private String duration;
        private String instructions;
    }
} 