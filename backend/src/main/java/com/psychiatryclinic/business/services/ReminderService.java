package com.psychiatryclinic.business.services;

import com.psychiatryclinic.core.utilities.email.EmailService;
import com.psychiatryclinic.dataAccess.AppointmentRepository;
import com.psychiatryclinic.entities.Appointment;
import com.psychiatryclinic.entities.enums.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderService {
    private static final Logger logger = LoggerFactory.getLogger(ReminderService.class);
    
    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    // Her saat başı çalışır
    @Scheduled(cron = "0 0 * * * *")
    public void sendAppointmentReminders() {
        logger.info("Randevu hatırlatma kontrolü başlatılıyor...");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayLater = now.plusDays(1);
        LocalDateTime oneHourLater = now.plusHours(1);

        // 24 saat kalan randevular
        List<Appointment> dayReminders = appointmentRepository.findByAppointmentDateTimeBetweenAndStatus(
            now, oneDayLater, AppointmentStatus.SCHEDULED
        );

        // 1 saat kalan randevular
        List<Appointment> hourReminders = appointmentRepository.findByAppointmentDateTimeBetweenAndStatus(
            now, oneHourLater, AppointmentStatus.SCHEDULED
        );

        sendDayReminders(dayReminders);
        sendHourReminders(hourReminders);
        
        logger.info("Randevu hatırlatmaları tamamlandı");
    }

    private void sendDayReminders(List<Appointment> appointments) {
        for (Appointment appointment : appointments) {
            try {
                emailService.sendAppointmentDayReminder(
                    appointment.getPatientId(),
                    appointment.getDoctorId(),
                    appointment.getAppointmentDateTime()
                );
                logger.info("24 saat hatırlatması gönderildi - Randevu ID: {}", appointment.getId());
            } catch (Exception e) {
                logger.error("Hatırlatma gönderilemedi - Randevu ID: {}", appointment.getId(), e);
            }
        }
    }

    private void sendHourReminders(List<Appointment> appointments) {
        for (Appointment appointment : appointments) {
            try {
                emailService.sendAppointmentHourReminder(
                    appointment.getPatientId(),
                    appointment.getDoctorId(),
                    appointment.getAppointmentDateTime()
                );
                logger.info("1 saat hatırlatması gönderildi - Randevu ID: {}", appointment.getId());
            } catch (Exception e) {
                logger.error("Hatırlatma gönderilemedi - Randevu ID: {}", appointment.getId(), e);
            }
        }
    }
} 