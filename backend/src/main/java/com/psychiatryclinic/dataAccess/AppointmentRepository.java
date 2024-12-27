package com.psychiatryclinic.dataAccess;

import com.psychiatryclinic.entities.Appointment;
import com.psychiatryclinic.entities.enums.AppointmentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    List<Appointment> findByDoctorId(String doctorId);
    List<Appointment> findByPatientId(String patientId);
    List<Appointment> findByDoctorIdAndAppointmentDateTimeBetween(
        String doctorId, 
        LocalDateTime start, 
        LocalDateTime end
    );
    boolean existsByDoctorIdAndAppointmentDateTime(String doctorId, LocalDateTime dateTime);
    List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Appointment> findByAppointmentDateTimeBetweenAndStatus(
        LocalDateTime start, 
        LocalDateTime end, 
        AppointmentStatus status
    );
    boolean existsByDoctorIdAndAppointmentDateTimeBetween(
        String doctorId, 
        LocalDateTime start, 
        LocalDateTime end
    );
} 