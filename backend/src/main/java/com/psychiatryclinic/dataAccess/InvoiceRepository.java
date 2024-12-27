package com.psychiatryclinic.dataAccess;

import com.psychiatryclinic.entities.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, String> {
    List<Invoice> findByPatientId(String patientId);
    List<Invoice> findByAppointmentId(String appointmentId);
    List<Invoice> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Invoice> findByIsPaid(boolean isPaid);
} 