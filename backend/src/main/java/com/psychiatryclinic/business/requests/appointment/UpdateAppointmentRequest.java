package com.psychiatryclinic.business.requests.appointment;

import com.psychiatryclinic.entities.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentRequest {
    private String id;
    private LocalDateTime appointmentDateTime;
    private String notes;
    private AppointmentStatus status;
} 