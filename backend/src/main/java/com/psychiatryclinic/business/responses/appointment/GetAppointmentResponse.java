package com.psychiatryclinic.business.responses.appointment;

import com.psychiatryclinic.business.responses.doctor.GetDoctorResponse;
import com.psychiatryclinic.business.responses.patient.GetPatientResponse;
import com.psychiatryclinic.entities.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAppointmentResponse {
    private String id;
    private GetPatientResponse patient;
    private GetDoctorResponse doctor;
    private String patientId;
    private String doctorId;
    private LocalDateTime appointmentDateTime;
    private AppointmentStatus status;
    private String notes;
    private String complaint;
    private boolean isOnline;
    private String meetingLink;
} 