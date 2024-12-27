package com.psychiatryclinic.business.abstracts;

import com.psychiatryclinic.business.requests.medication.CreateMedicationRequest;
import com.psychiatryclinic.business.responses.medication.GetAllMedicationsResponse;
import com.psychiatryclinic.business.responses.medication.GetMedicationResponse;

import java.util.List;

public interface MedicationService {
    void add(CreateMedicationRequest createMedicationRequest);
    void delete(String id);
    void updateStatus(String id, boolean isActive);
    GetMedicationResponse getById(String id);
    List<GetAllMedicationsResponse> getAll();
    List<GetMedicationResponse> getByPatientId(String patientId);
    List<GetMedicationResponse> getByDoctorId(String doctorId);
    List<GetMedicationResponse> getActiveByPatientId(String patientId);
} 