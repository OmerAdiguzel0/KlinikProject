package com.psychiatryclinic.business.responses.treatment;

import com.psychiatryclinic.entities.enums.TreatmentStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GetAllTreatmentsResponse {
    private String id;
    private String patientName;
    private String doctorName;
    private String diagnosis;
    private LocalDateTime startDate;
    private TreatmentStatus status;
} 