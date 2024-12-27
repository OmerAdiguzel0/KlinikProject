package com.psychiatryclinic.dataAccess;

import com.psychiatryclinic.entities.Doctor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends MongoRepository<Doctor, String> {
    List<Doctor> findBySpecialization(String specialization);
    boolean existsByLicenseNumber(String licenseNumber);
    void deleteByUserId(String userId);
    boolean existsByUserId(String userId);
} 