package com.psychiatryclinic.business.requests.appointment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {
    private String patientId;
    private String doctorId;
    private LocalDateTime appointmentDateTime;
    private String notes;
} 