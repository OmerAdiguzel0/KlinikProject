package com.psychiatryclinic.entities;

import com.psychiatryclinic.entities.enums.BloodType;
import com.psychiatryclinic.entities.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "patients")
public class Patient {
    @Id
    private String id;
    
    @DBRef
    private User user;
    
    @DBRef
    private Doctor doctor;
    
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private String emergencyContact;
    private String emergencyContactPhone;
    private List<String> allergies;
    private List<String> chronicDiseases;
    private BloodType bloodType;
    private String notes;
} 