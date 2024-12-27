package com.psychiatryclinic.business.abstracts;

import com.psychiatryclinic.business.requests.workinghours.CreateWorkingHoursRequest;
import com.psychiatryclinic.business.responses.workinghours.GetWorkingHoursResponse;
import com.psychiatryclinic.entities.enums.DayOfWeek;

import java.util.List;

public interface WorkingHoursService {
    void add(CreateWorkingHoursRequest createWorkingHoursRequest);
    void delete(String id);
    GetWorkingHoursResponse getById(String id);
    List<GetWorkingHoursResponse> getByDoctorId(String doctorId);
    List<GetWorkingHoursResponse> getByDoctorIdAndDay(String doctorId, DayOfWeek dayOfWeek);
    List<GetWorkingHoursResponse> getAll();
} 