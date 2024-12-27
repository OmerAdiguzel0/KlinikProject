package com.psychiatryclinic.business.responses.payment;

import com.psychiatryclinic.business.responses.appointment.GetAppointmentResponse;
import com.psychiatryclinic.business.responses.doctor.GetDoctorResponse;
import com.psychiatryclinic.business.responses.patient.GetPatientResponse;
import com.psychiatryclinic.entities.enums.PaymentStatus;
import com.psychiatryclinic.entities.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPaymentResponse {
    private String id;
    private GetPatientResponse patient;
    private GetDoctorResponse doctor;
    private GetAppointmentResponse appointment;
    private String patientId;
    private String doctorId;
    private String appointmentId;
    private BigDecimal amount;
    private PaymentType type;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private String transactionId;
    private String notes;
} 