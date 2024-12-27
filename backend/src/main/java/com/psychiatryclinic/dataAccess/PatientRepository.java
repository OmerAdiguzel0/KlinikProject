package com.psychiatryclinic.dataAccess;

import com.psychiatryclinic.entities.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {
    Optional<Patient> findByUser_Email(String email);
    List<Patient> findByDoctor_Id(String doctorId);
    @Query(value = "{ '_id' : ?0 }", fields = "{ 'user' : 1 }")
    Patient findPatientWithUser(String patientId);
    void deleteByUserId(String userId);
    boolean existsByUserId(String userId);
} 