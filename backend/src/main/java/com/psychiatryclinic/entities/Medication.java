package com.psychiatryclinic.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "medications")
public class Medication {
    @Id
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