package com.psychiatryclinic.business.concretes;

import com.psychiatryclinic.business.abstracts.WorkingHoursService;
import com.psychiatryclinic.business.requests.workinghours.CreateWorkingHoursRequest;
import com.psychiatryclinic.business.responses.workinghours.GetWorkingHoursResponse;
import com.psychiatryclinic.business.rules.WorkingHoursBusinessRules;
import com.psychiatryclinic.core.utilities.mappers.ModelMapperService;
import com.psychiatryclinic.dataAccess.WorkingHoursRepository;
import com.psychiatryclinic.entities.WorkingHours;
import com.psychiatryclinic.entities.enums.DayOfWeek;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WorkingHoursManager implements WorkingHoursService {
    private WorkingHoursRepository workingHoursRepository;
    private ModelMapperService modelMapperService;
    private WorkingHoursBusinessRules workingHoursBusinessRules;

    @Override
    public void add(CreateWorkingHoursRequest createWorkingHoursRequest) {
        workingHoursBusinessRules.checkIfDoctorExists(createWorkingHoursRequest.getDoctorId());
        workingHoursBusinessRules.checkIfTimeValid(
            createWorkingHoursRequest.getStartTime(), 
            createWorkingHoursRequest.getEndTime()
        );
        workingHoursBusinessRules.checkIfDayAndDoctorExists(
            createWorkingHoursRequest.getDoctorId(), 
            createWorkingHoursRequest.getDayOfWeek()
        );
        workingHoursBusinessRules.checkIfSlotDurationValid(
            createWorkingHoursRequest.getSlotDurationMinutes()
        );

        WorkingHours workingHours = modelMapperService.forRequest()
            .map(createWorkingHoursRequest, WorkingHours.class);

        workingHoursRepository.save(workingHours);
    }

    @Override
    public void delete(String id) {
        workingHoursBusinessRules.checkIfWorkingHoursExists(id);
        workingHoursRepository.deleteById(id);
    }

    @Override
    public GetWorkingHoursResponse getById(String id) {
        WorkingHours workingHours = workingHoursRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Çalışma saati bulunamadı"));

        return modelMapperService.forResponse()
            .map(workingHours, GetWorkingHoursResponse.class);
    }

    @Override
    public List<GetWorkingHoursResponse> getByDoctorId(String doctorId) {
        workingHoursBusinessRules.checkIfDoctorExists(doctorId);
        List<WorkingHours> workingHours = workingHoursRepository.findByDoctorId(doctorId);

        return workingHours.stream()
            .map(hours -> modelMapperService.forResponse()
                .map(hours, GetWorkingHoursResponse.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<GetWorkingHoursResponse> getByDoctorIdAndDay(String doctorId, DayOfWeek dayOfWeek) {
        workingHoursBusinessRules.checkIfDoctorExists(doctorId);
        List<WorkingHours> workingHours = workingHoursRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek);

        return workingHours.stream()
            .map(hours -> modelMapperService.forResponse()
                .map(hours, GetWorkingHoursResponse.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<GetWorkingHoursResponse> getAll() {
        List<WorkingHours> workingHours = workingHoursRepository.findAll();

        return workingHours.stream()
            .map(hours -> modelMapperService.forResponse()
                .map(hours, GetWorkingHoursResponse.class))
            .collect(Collectors.toList());
    }
} 