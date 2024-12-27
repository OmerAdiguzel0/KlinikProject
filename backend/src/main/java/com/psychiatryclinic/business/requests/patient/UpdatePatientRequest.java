package com.psychiatryclinic.business.requests.patient;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class UpdatePatientRequest {
    private String id;
    private String address;
    private String emergencyContact;
    private String emergencyContactPhone;
    private List<String> allergies;
    private List<String> chronicDiseases;
    private String notes;
} 