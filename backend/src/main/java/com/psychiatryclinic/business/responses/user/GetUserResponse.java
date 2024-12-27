package com.psychiatryclinic.business.responses.user;

import com.psychiatryclinic.entities.enums.UserRole;
import lombok.Data;

@Data
public class GetUserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private String profilePhotoUrl;
} 