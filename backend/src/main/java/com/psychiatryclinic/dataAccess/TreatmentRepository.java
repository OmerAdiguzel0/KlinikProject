package com.psychiatryclinic.dataAccess;

import com.psychiatryclinic.entities.Treatment;
import com.psychiatryclinic.entities.enums.TreatmentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TreatmentRepository extends MongoRepository<Treatment, String> {
    List<Treatment> findByPatientId(String patientId);
    List<Treatment> findByDoctorId(String doctorId);
    List<Treatment> findByStatus(TreatmentStatus status);
} 