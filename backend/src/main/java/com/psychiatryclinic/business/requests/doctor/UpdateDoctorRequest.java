package com.psychiatryclinic.business.requests.doctor;

import lombok.Data;
import java.util.List;
import com.psychiatryclinic.entities.enums.Specialization;

@Data
public class UpdateDoctorRequest {
    private String id;
    private Specialization specialization;
    private String education;
    private List<String> certificates;
    private String about;
    private List<WorkingHoursRequest> workingHours;

    @Data
    public static class WorkingHoursRequest {
        private String dayOfWeek;
        private String startTime;
        private String endTime;
    }
} 