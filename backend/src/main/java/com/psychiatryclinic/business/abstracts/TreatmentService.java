package com.psychiatryclinic.business.abstracts;

import com.psychiatryclinic.business.requests.treatment.CreateTreatmentRequest;
import com.psychiatryclinic.business.requests.treatment.UpdateTreatmentRequest;
import com.psychiatryclinic.business.responses.treatment.GetAllTreatmentsResponse;
import com.psychiatryclinic.business.responses.treatment.GetTreatmentResponse;
import com.psychiatryclinic.entities.enums.TreatmentStatus;

import java.util.List;

public interface TreatmentService {
    void add(CreateTreatmentRequest createTreatmentRequest);
    void update(UpdateTreatmentRequest updateTreatmentRequest);
    void delete(String id);
    void updateStatus(String id, TreatmentStatus status);
    GetTreatmentResponse getById(String id);
    List<GetAllTreatmentsResponse> getAll();
    List<GetTreatmentResponse> getByPatientId(String patientId);
    List<GetTreatmentResponse> getByDoctorId(String doctorId);
    List<GetTreatmentResponse> getByStatus(TreatmentStatus status);
} 