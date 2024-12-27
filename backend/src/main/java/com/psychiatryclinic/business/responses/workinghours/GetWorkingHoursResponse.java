package com.psychiatryclinic.business.responses.workinghours;

import com.psychiatryclinic.business.responses.doctor.GetDoctorResponse;
import com.psychiatryclinic.entities.enums.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetWorkingHoursResponse {
    private String id;
    private GetDoctorResponse doctor;
    private String doctorId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isAvailable;
    private String notes;
} 