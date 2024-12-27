package com.psychiatryclinic.business.rules;

import com.psychiatryclinic.core.exceptions.BusinessException;
import com.psychiatryclinic.dataAccess.WorkingHoursRepository;
import com.psychiatryclinic.entities.WorkingHours;
import com.psychiatryclinic.entities.enums.DayOfWeek;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@AllArgsConstructor
public class WorkingHoursBusinessRules {
    private WorkingHoursRepository workingHoursRepository;
    private DoctorBusinessRules doctorBusinessRules;

    public void checkIfDoctorExists(String doctorId) {
        doctorBusinessRules.checkIfDoctorExists(doctorId);
    }

    public void checkIfWorkingHoursExists(String id) {
        if (!workingHoursRepository.existsById(id)) {
            throw new BusinessException("Çalışma saati kaydı bulunamadı");
        }
    }

    public void checkIfTimeValid(LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new BusinessException("Başlangıç saati bitiş saatinden sonra olamaz");
        }
    }

    public void checkIfDayAndDoctorExists(String doctorId, DayOfWeek dayOfWeek) {
        if (workingHoursRepository.existsByDoctorIdAndDayOfWeek(doctorId, dayOfWeek)) {
            throw new BusinessException("Bu gün için zaten çalışma saati tanımlanmış");
        }
    }

    public void checkIfSlotDurationValid(int slotDurationMinutes) {
        if (slotDurationMinutes < 15 || slotDurationMinutes > 120) {
            throw new BusinessException("Randevu süresi 15-120 dakika arasında olmalıdır");
        }
    }
} 