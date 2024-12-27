package com.psychiatryclinic.entities;

import com.psychiatryclinic.entities.enums.TreatmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "treatments")
public class Treatment {
    @Id
    private String id;
    private String patientId;
    private String doctorId;
    private String diagnosis;
    private String treatmentPlan;
    private String notes;
    private LocalDateTime treatmentDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime nextAppointment;
    private TreatmentStatus status;
} 