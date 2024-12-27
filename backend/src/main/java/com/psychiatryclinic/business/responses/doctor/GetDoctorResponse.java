package com.psychiatryclinic.business.responses.doctor;

import com.psychiatryclinic.business.responses.user.GetUserResponse;
import lombok.Data;
import java.util.List;
import com.psychiatryclinic.entities.enums.Specialization;

@Data
public class GetDoctorResponse {
    private String id;
    private GetUserResponse user;
    private String title;
    private Specialization specialization;
    private String licenseNumber;
    private String education;
    private List<String> certificates;
    private String about;
    private List<WorkingHoursResponse> workingHours;
    private boolean isAvailable;

    @Data
    public static class WorkingHoursResponse {
        private String dayOfWeek;
        private String startTime;
        private String endTime;
    }
} 