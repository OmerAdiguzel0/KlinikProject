package com.psychiatryclinic.business.requests.user;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePhotoUrl;

    // Getter ve Setter'lar
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Diğer getter ve setter'lar...
} 