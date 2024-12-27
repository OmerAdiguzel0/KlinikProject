package com.psychiatryclinic.business.responses.appointment;

import com.psychiatryclinic.entities.enums.AppointmentStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GetAllAppointmentsResponse {
    private String id;
    private String patientName;
    private String doctorName;
    private LocalDateTime appointmentDateTime;
    private AppointmentStatus status;
    private boolean isOnline;
} 