package com.psychiatryclinic.dataAccess;

import com.psychiatryclinic.entities.Payment;
import com.psychiatryclinic.entities.enums.PaymentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByAppointment_Id(String appointmentId);
    List<Payment> findByPatientId(String patientId);
    List<Payment> findByDoctorId(String doctorId);
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);
} 