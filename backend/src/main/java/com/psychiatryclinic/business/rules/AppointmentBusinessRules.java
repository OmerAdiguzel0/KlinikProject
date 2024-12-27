package com.psychiatryclinic.business.rules;

import com.psychiatryclinic.business.constants.Messages;
import com.psychiatryclinic.core.exceptions.BusinessException;
import com.psychiatryclinic.dataAccess.AppointmentRepository;
import com.psychiatryclinic.dataAccess.DoctorRepository;
import com.psychiatryclinic.dataAccess.PatientRepository;
import com.psychiatryclinic.dataAccess.WorkingHoursRepository;
import com.psychiatryclinic.entities.Appointment;
import com.psychiatryclinic.entities.WorkingHours;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
public class AppointmentBusinessRules {
    private AppointmentRepository appointmentRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private WorkingHoursRepository workingHoursRepository;
    private DoctorBusinessRules doctorBusinessRules;
    private PatientBusinessRules patientBusinessRules;

    public void checkIfDoctorExists(String doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new BusinessException(Messages.Doctor.NOT_EXISTS);
        }
    }

    public void checkIfPatientExists(String patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new BusinessException(Messages.Patient.NOT_EXISTS);
        }
    }

    public void checkIfDoctorAvailable(String doctorId, LocalDateTime dateTime) {
        boolean isBooked = appointmentRepository.existsByDoctorIdAndAppointmentDateTime(doctorId, dateTime);
        if (isBooked) {
            throw new BusinessException(Messages.Appointment.DOCTOR_NOT_AVAILABLE);
        }
    }

    public void checkIfAppointmentExists(String id) {
        if (!appointmentRepository.existsById(id)) {
            throw new BusinessException(Messages.Appointment.NOT_EXISTS);
        }
    }

    public void checkIfAppointmentTimeValid(LocalDateTime dateTime) {
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException(Messages.Appointment.INVALID_TIME);
        }
    }

    public void checkIfPatientHasAppointment(String patientId, LocalDateTime dateTime) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentDateTime().equals(dateTime)) {
                throw new BusinessException("Hasta için bu saatte zaten bir randevu bulunmaktadır");
            }
        }
    }

    public void checkIfDoctorAvailableForDay(String doctorId, LocalDateTime dateTime) {
        var dayOfWeek = convertToDayOfWeek(dateTime.getDayOfWeek());
        List<WorkingHours> workingHours = workingHoursRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek);
        
        if (workingHours.isEmpty()) {
            throw new BusinessException("Doktor bu gün çalışmamaktadır");
        }

        WorkingHours doctorSchedule = workingHours.get(0);
        if (!doctorSchedule.isAvailable()) {
            throw new BusinessException("Doktor bu gün müsait değildir");
        }

        LocalTime appointmentTime = dateTime.toLocalTime();
        if (appointmentTime.isBefore(doctorSchedule.getStartTime()) || 
            appointmentTime.isAfter(doctorSchedule.getEndTime())) {
            throw new BusinessException("Randevu saati doktorun çalışma saatleri dışındadır");
        }
    }

    public void checkIfSlotAvailable(String doctorId, LocalDateTime dateTime) {
        var dayOfWeek = convertToDayOfWeek(dateTime.getDayOfWeek());
        WorkingHours workingHours = workingHoursRepository
            .findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek)
            .get(0);

        LocalTime appointmentTime = dateTime.toLocalTime();
        int slotDuration = workingHours.getSlotDurationMinutes();

        LocalDateTime slotStart = dateTime.minusMinutes(slotDuration);
        LocalDateTime slotEnd = dateTime.plusMinutes(slotDuration);

        boolean hasConflict = appointmentRepository
            .existsByDoctorIdAndAppointmentDateTimeBetween(doctorId, slotStart, slotEnd);

        if (hasConflict) {
            throw new BusinessException("Bu zaman diliminde başka bir randevu bulunmaktadır");
        }
    }

    private com.psychiatryclinic.entities.enums.DayOfWeek convertToDayOfWeek(java.time.DayOfWeek javaDay) {
        return switch (javaDay) {
            case MONDAY -> com.psychiatryclinic.entities.enums.DayOfWeek.MONDAY;
            case TUESDAY -> com.psychiatryclinic.entities.enums.DayOfWeek.TUESDAY;
            case WEDNESDAY -> com.psychiatryclinic.entities.enums.DayOfWeek.WEDNESDAY;
            case THURSDAY -> com.psychiatryclinic.entities.enums.DayOfWeek.THURSDAY;
            case FRIDAY -> com.psychiatryclinic.entities.enums.DayOfWeek.FRIDAY;
            case SATURDAY -> com.psychiatryclinic.entities.enums.DayOfWeek.SATURDAY;
            case SUNDAY -> com.psychiatryclinic.entities.enums.DayOfWeek.SUNDAY;
        };
    }
} 