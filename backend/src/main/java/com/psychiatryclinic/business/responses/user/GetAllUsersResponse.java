package com.psychiatryclinic.business.responses.user;

import com.psychiatryclinic.entities.enums.UserRole;
import lombok.Data;

@Data
public class GetAllUsersResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
} 