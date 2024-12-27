package com.psychiatryclinic.business.concretes;

import com.psychiatryclinic.business.abstracts.PatientService;
import com.psychiatryclinic.business.requests.patient.CreatePatientRequest;
import com.psychiatryclinic.business.requests.patient.UpdatePatientRequest;
import com.psychiatryclinic.business.responses.patient.GetAllPatientsResponse;
import com.psychiatryclinic.business.responses.patient.GetPatientResponse;
import com.psychiatryclinic.business.rules.PatientBusinessRules;
import com.psychiatryclinic.core.utilities.mappers.ModelMapperService;
import com.psychiatryclinic.dataAccess.PatientRepository;
import com.psychiatryclinic.entities.Patient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PatientManager implements PatientService {
    private PatientRepository patientRepository;
    private ModelMapperService modelMapperService;
    private PatientBusinessRules patientBusinessRules;

    @Override
    public void add(CreatePatientRequest createPatientRequest) {
        patientBusinessRules.checkIfUserExists(createPatientRequest.getUserId());
        
        Patient patient = this.modelMapperService.forRequest()
                .map(createPatientRequest, Patient.class);
        
        patientRepository.save(patient);
    }

    @Override
    public void update(UpdatePatientRequest updatePatientRequest) {
        patientBusinessRules.checkIfPatientExists(updatePatientRequest.getId());
        
        Patient patient = this.modelMapperService.forRequest()
                .map(updatePatientRequest, Patient.class);
        
        patientRepository.save(patient);
    }

    @Override
    public void delete(String id) {
        patientBusinessRules.checkIfPatientExists(id);
        patientRepository.deleteById(id);
    }

    @Override
    public GetPatientResponse getById(String id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı"));
        
        return modelMapperService.forResponse()
                .map(patient, GetPatientResponse.class);
    }

    @Override
    public List<GetAllPatientsResponse> getAll() {
        List<Patient> patients = patientRepository.findAll();
        
        return patients.stream()
                .map(patient -> modelMapperService.forResponse()
                        .map(patient, GetAllPatientsResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<GetPatientResponse> getByDoctorId(String doctorId) {
        List<Patient> patients = patientRepository.findByDoctor_Id(doctorId);
        
        return patients.stream()
                .map(patient -> modelMapperService.forResponse()
                        .map(patient, GetPatientResponse.class))
                .collect(Collectors.toList());
    }
} 