package com.psychiatryclinic.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import com.psychiatryclinic.entities.enums.Specialization;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "doctors")
public class Doctor {
    @Id
    private String id;
    
    @DBRef
    private User user;
    
    private String title;
    private Specialization specialization;
    private boolean isActive;
    private String licenseNumber;
    private String education;
    private List<String> certificates;
    private String about;
    private List<String> workingDays;
    private String workingHours;
    private int sessionDuration; // dakika cinsinden
    private boolean isAvailable;
    private String specialty;
    private List<String> specializations;
} 