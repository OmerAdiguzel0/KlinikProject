package com.psychiatryclinic.business.concretes;

import com.psychiatryclinic.business.abstracts.TreatmentService;
import com.psychiatryclinic.business.requests.treatment.CreateTreatmentRequest;
import com.psychiatryclinic.business.requests.treatment.UpdateTreatmentRequest;
import com.psychiatryclinic.business.responses.treatment.GetAllTreatmentsResponse;
import com.psychiatryclinic.business.responses.treatment.GetTreatmentResponse;
import com.psychiatryclinic.business.rules.TreatmentBusinessRules;
import com.psychiatryclinic.core.utilities.mappers.ModelMapperService;
import com.psychiatryclinic.dataAccess.TreatmentRepository;
import com.psychiatryclinic.entities.Treatment;
import com.psychiatryclinic.entities.enums.TreatmentStatus;
import com.psychiatryclinic.core.exceptions.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TreatmentManager implements TreatmentService {
    private TreatmentRepository treatmentRepository;
    private ModelMapperService modelMapperService;
    private TreatmentBusinessRules rules;

    @Override
    public void add(CreateTreatmentRequest createTreatmentRequest) {
        Treatment treatment = this.modelMapperService.forRequest()
                .map(createTreatmentRequest, Treatment.class);
        
        treatment.setStatus(TreatmentStatus.ACTIVE);
        treatmentRepository.save(treatment);
    }

    @Override
    public void update(UpdateTreatmentRequest updateTreatmentRequest) {
        rules.checkIfTreatmentExists(updateTreatmentRequest.getId());
        
        Treatment treatment = this.modelMapperService.forRequest()
                .map(updateTreatmentRequest, Treatment.class);
        
        treatmentRepository.save(treatment);
    }

    @Override
    public void delete(String id) {
        rules.checkIfTreatmentExists(id);
        treatmentRepository.deleteById(id);
    }

    @Override
    public GetTreatmentResponse getById(String id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tedavi bulunamadı"));
        
        return modelMapperService.forResponse()
                .map(treatment, GetTreatmentResponse.class);
    }

    @Override
    public List<GetAllTreatmentsResponse> getAll() {
        List<Treatment> treatments = treatmentRepository.findAll();
        
        return treatments.stream()
                .map(treatment -> modelMapperService.forResponse()
                        .map(treatment, GetAllTreatmentsResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<GetTreatmentResponse> getByPatientId(String patientId) {
        List<Treatment> treatments = treatmentRepository.findByPatientId(patientId);
        
        return treatments.stream()
                .map(treatment -> modelMapperService.forResponse()
                        .map(treatment, GetTreatmentResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<GetTreatmentResponse> getByDoctorId(String doctorId) {
        List<Treatment> treatments = treatmentRepository.findByDoctorId(doctorId);
        
        return treatments.stream()
                .map(treatment -> modelMapperService.forResponse()
                        .map(treatment, GetTreatmentResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<GetTreatmentResponse> getByStatus(TreatmentStatus status) {
        List<Treatment> treatments = treatmentRepository.findByStatus(status);
        
        return treatments.stream()
                .map(treatment -> modelMapperService.forResponse()
                        .map(treatment, GetTreatmentResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updateStatus(String id, TreatmentStatus status) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Tedavi bulunamadı"));
        
        treatment.setStatus(status);
        treatmentRepository.save(treatment);
    }
} 