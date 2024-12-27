package com.psychiatryclinic.business.abstracts;

import com.psychiatryclinic.business.requests.patient.CreatePatientRequest;
import com.psychiatryclinic.business.requests.patient.UpdatePatientRequest;
import com.psychiatryclinic.business.responses.patient.GetAllPatientsResponse;
import com.psychiatryclinic.business.responses.patient.GetPatientResponse;

import java.util.List;

public interface PatientService {
    void add(CreatePatientRequest createPatientRequest);
    void update(UpdatePatientRequest updatePatientRequest);
    void delete(String id);
    GetPatientResponse getById(String id);
    List<GetAllPatientsResponse> getAll();
    List<GetPatientResponse> getByDoctorId(String doctorId);
} 