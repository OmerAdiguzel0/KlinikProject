package com.psychiatryclinic.dataAccess;

import com.psychiatryclinic.entities.WorkingHours;
import com.psychiatryclinic.entities.enums.DayOfWeek;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WorkingHoursRepository extends MongoRepository<WorkingHours, String> {
    List<WorkingHours> findByDoctorId(String doctorId);
    List<WorkingHours> findByDoctorIdAndDayOfWeek(String doctorId, DayOfWeek dayOfWeek);
    boolean existsByDoctorIdAndDayOfWeek(String doctorId, DayOfWeek dayOfWeek);
} 