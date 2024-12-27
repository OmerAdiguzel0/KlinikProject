package com.psychiatryclinic.business.abstracts;

import com.psychiatryclinic.business.requests.user.CreateUserRequest;
import com.psychiatryclinic.business.requests.user.UpdateUserRequest;
import com.psychiatryclinic.business.responses.user.GetAllUsersResponse;
import com.psychiatryclinic.business.responses.user.GetUserResponse;
import com.psychiatryclinic.entities.Doctor;
import com.psychiatryclinic.entities.Patient;

import java.util.List;

public interface UserService {
    void add(CreateUserRequest createUserRequest);
    void update(UpdateUserRequest updateUserRequest);
    void delete(String id);
    GetUserResponse getById(String id);
    GetUserResponse getByEmail(String email);
    List<GetAllUsersResponse> getAll();
    Patient getPatientById(String id);
    Doctor getDoctorById(String id);
} 