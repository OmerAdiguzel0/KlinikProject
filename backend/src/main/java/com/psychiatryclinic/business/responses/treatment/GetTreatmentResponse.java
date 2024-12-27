package com.psychiatryclinic.business.responses.treatment;

import com.psychiatryclinic.business.responses.doctor.GetDoctorResponse;
import com.psychiatryclinic.business.responses.patient.GetPatientResponse;
import com.psychiatryclinic.entities.enums.TreatmentStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetTreatmentResponse {
    private String id;
    private GetPatientResponse patient;
    private GetDoctorResponse doctor;
    private String diagnosis;
    private String treatmentPlan;
    private List<MedicationResponse> medications;
    private String notes;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private TreatmentStatus status;

    @Data
    public static class MedicationResponse {
        private String name;
        private String dosage;
        private String frequency;
        private String duration;
        private String instructions;
    }
} 