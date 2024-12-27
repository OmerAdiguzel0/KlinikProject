package com.psychiatryclinic.business.concretes;

import com.psychiatryclinic.business.abstracts.DoctorService;
import com.psychiatryclinic.business.requests.doctor.CreateDoctorRequest;
import com.psychiatryclinic.business.requests.doctor.UpdateDoctorRequest;
import com.psychiatryclinic.business.responses.doctor.GetAllDoctorsResponse;
import com.psychiatryclinic.business.responses.doctor.GetDoctorResponse;
import com.psychiatryclinic.business.rules.DoctorBusinessRules;
import com.psychiatryclinic.core.utilities.mappers.ModelMapperService;
import com.psychiatryclinic.dataAccess.DoctorRepository;
import com.psychiatryclinic.entities.Doctor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DoctorManager implements DoctorService {
    private DoctorRepository doctorRepository;
    private ModelMapperService modelMapperService;
    private DoctorBusinessRules doctorBusinessRules;

    @Override
    public void add(CreateDoctorRequest createDoctorRequest) {
        doctorBusinessRules.checkIfUserExists(createDoctorRequest.getUserId());
        
        Doctor doctor = this.modelMapperService.forRequest()
                .map(createDoctorRequest, Doctor.class);
        
        doctorRepository.save(doctor);
    }

    @Override
    public void update(UpdateDoctorRequest updateDoctorRequest) {
        doctorBusinessRules.checkIfDoctorExists(updateDoctorRequest.getId());
        
        Doctor doctor = this.modelMapperService.forRequest()
                .map(updateDoctorRequest, Doctor.class);
        
        doctorRepository.save(doctor);
    }

    @Override
    public void delete(String id) {
        doctorBusinessRules.checkIfDoctorExists(id);
        doctorRepository.deleteById(id);
    }

    @Override
    public GetDoctorResponse getById(String id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));
        
        return modelMapperService.forResponse()
                .map(doctor, GetDoctorResponse.class);
    }

    @Override
    public List<GetAllDoctorsResponse> getAll() {
        List<Doctor> doctors = doctorRepository.findAll();
        
        return doctors.stream()
                .map(doctor -> {
                    GetAllDoctorsResponse response = modelMapperService.forResponse()
                            .map(doctor, GetAllDoctorsResponse.class);
                    if (doctor.getUser() != null) {
                        response.setFirstName(doctor.getUser().getFirstName());
                        response.setLastName(doctor.getUser().getLastName());
                        response.setEmail(doctor.getUser().getEmail());
                        response.setPhoneNumber(doctor.getUser().getPhoneNumber());
                    }
                    response.setAvailable(doctor.isAvailable());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<GetDoctorResponse> getBySpecialization(String specialization) {
        List<Doctor> doctors = doctorRepository.findBySpecialization(specialization);
        
        return doctors.stream()
                .map(doctor -> modelMapperService.forResponse()
                        .map(doctor, GetDoctorResponse.class))
                .collect(Collectors.toList());
    }
} 