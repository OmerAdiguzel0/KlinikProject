package com.psychiatryclinic.business.responses.patient;

import com.psychiatryclinic.business.responses.user.GetUserResponse;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class GetPatientResponse {
    private String id;
    private GetUserResponse user;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String emergencyContact;
    private String emergencyContactPhone;
    private List<String> allergies;
    private List<String> chronicDiseases;
    private String bloodType;
    private String notes;
} 