package com.psychiatryclinic.dataAccess;

import com.psychiatryclinic.entities.Medication;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MedicationRepository extends MongoRepository<Medication, String> {
    List<Medication> findByPatientId(String patientId);
    List<Medication> findByDoctorId(String doctorId);
    List<Medication> findByPatientIdAndIsActive(String patientId, boolean isActive);
} 