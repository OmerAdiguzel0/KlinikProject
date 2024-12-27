package com.psychiatryclinic.business.abstracts;

import com.psychiatryclinic.business.requests.doctor.CreateDoctorRequest;
import com.psychiatryclinic.business.requests.doctor.UpdateDoctorRequest;
import com.psychiatryclinic.business.responses.doctor.GetAllDoctorsResponse;
import com.psychiatryclinic.business.responses.doctor.GetDoctorResponse;

import java.util.List;

public interface DoctorService {
    void add(CreateDoctorRequest createDoctorRequest);
    void update(UpdateDoctorRequest updateDoctorRequest);
    void delete(String id);
    GetDoctorResponse getById(String id);
    List<GetAllDoctorsResponse> getAll();
    List<GetDoctorResponse> getBySpecialization(String specialization);
} 