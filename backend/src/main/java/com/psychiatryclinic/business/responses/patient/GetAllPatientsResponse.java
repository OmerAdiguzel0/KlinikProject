package com.psychiatryclinic.business.responses.patient;

import lombok.Data;
import java.time.LocalDate;

@Data
public class GetAllPatientsResponse {
    private String id;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String bloodType;
} 