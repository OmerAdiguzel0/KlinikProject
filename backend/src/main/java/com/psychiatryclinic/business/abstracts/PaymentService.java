package com.psychiatryclinic.business.abstracts;

import com.psychiatryclinic.business.requests.payment.CreatePaymentRequest;
import com.psychiatryclinic.business.responses.payment.GetPaymentResponse;
import com.psychiatryclinic.entities.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {
    void add(CreatePaymentRequest createPaymentRequest);
    void updateStatus(String id, PaymentStatus status);
    GetPaymentResponse getById(String id);
    List<GetPaymentResponse> getByAppointmentId(String appointmentId);
    List<GetPaymentResponse> getByPatientId(String patientId);
    List<GetPaymentResponse> getByDoctorId(String doctorId);
    List<GetPaymentResponse> getByStatus(PaymentStatus status);
    List<GetPaymentResponse> getByDateRange(LocalDateTime start, LocalDateTime end);
    List<GetPaymentResponse> getAll();
} 