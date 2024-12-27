package com.psychiatryclinic.business.responses.doctor;

import com.psychiatryclinic.entities.enums.Specialization;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllDoctorsResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String title;
    private Specialization specialization;
    private String about;
    private String education;
    private List<String> certificates;
    private boolean available;
} 