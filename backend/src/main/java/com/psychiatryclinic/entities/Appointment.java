package com.psychiatryclinic.entities;

import com.psychiatryclinic.entities.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "appointments")
public class Appointment {
    @Id
    private String id;
    private String patientId;
    private String doctorId;
    private LocalDateTime appointmentDateTime;
    private String notes;
    private AppointmentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 