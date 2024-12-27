package com.psychiatryclinic.business.concretes;

import com.psychiatryclinic.business.abstracts.MedicationService;
import com.psychiatryclinic.business.requests.medication.CreateMedicationRequest;
import com.psychiatryclinic.business.responses.medication.GetAllMedicationsResponse;
import com.psychiatryclinic.business.responses.medication.GetMedicationResponse;
import com.psychiatryclinic.core.utilities.mappers.ModelMapperService;
import com.psychiatryclinic.dataAccess.MedicationRepository;
import com.psychiatryclinic.entities.Medication;
import com.psychiatryclinic.core.exceptions.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MedicationManager implements MedicationService {
    private MedicationRepository medicationRepository;
    private ModelMapperService modelMapperService;

    @Override
    public void add(CreateMedicationRequest createMedicationRequest) {
        Medication medication = this.modelMapperService.forRequest()
                .map(createMedicationRequest, Medication.class);
        
        medication.setActive(true);
        medicationRepository.save(medication);
    }

    @Override
    public void delete(String id) {
        if (!medicationRepository.existsById(id)) {
            throw new BusinessException("İlaç kaydı bulunamadı");
        }
        medicationRepository.deleteById(id);
    }

    @Override
    public void updateStatus(String id, boolean isActive) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("İlaç kaydı bulunamadı"));
        
        medication.setActive(isActive);
        medicationRepository.save(medication);
    }

    @Override
    public GetMedicationResponse getById(String id) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("İlaç kaydı bulunamadı"));
        
        return modelMapperService.forResponse()
                .map(medication, GetMedicationResponse.class);
    }

    @Override
    public List<GetAllMedicationsResponse> getAll() {
        List<Medication> medications = medicationRepository.findAll();
        
        return medications.stream()
                .map(medication -> modelMapperService.forResponse()
                        .map(medication, GetAllMedicationsResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<GetMedicationResponse> getByPatientId(String patientId) {
        List<Medication> medications = medicationRepository.findByPatientId(patientId);
        
        return medications.stream()
                .map(medication -> modelMapperService.forResponse()
                        .map(medication, GetMedicationResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<GetMedicationResponse> getByDoctorId(String doctorId) {
        List<Medication> medications = medicationRepository.findByDoctorId(doctorId);
        
        return medications.stream()
                .map(medication -> modelMapperService.forResponse()
                        .map(medication, GetMedicationResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<GetMedicationResponse> getActiveByPatientId(String patientId) {
        List<Medication> medications = medicationRepository.findByPatientIdAndIsActive(patientId, true);
        
        return medications.stream()
                .map(medication -> modelMapperService.forResponse()
                        .map(medication, GetMedicationResponse.class))
                .collect(Collectors.toList());
    }

    // ... diğer metodlar için devam edelim mi?
} 