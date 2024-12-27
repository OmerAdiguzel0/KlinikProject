package com.psychiatryclinic.core.scheduler;

import com.psychiatryclinic.business.abstracts.AppointmentService;
import com.psychiatryclinic.core.utilities.sms.SmsService;
import com.psychiatryclinic.entities.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class AppointmentReminderScheduler {
    private final AppointmentService appointmentService;
    private final SmsService smsService;

    @Scheduled(cron = "0 0 0 * * ?") // Her gün gece yarısı çalışır
    public void sendAppointmentReminders() {
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        LocalDateTime endOfTomorrow = tomorrow.plusDays(1);

        appointmentService.getByDateRange(tomorrow, endOfTomorrow)
            .forEach(appointment -> {
                try {
                    smsService.sendAppointmentReminder(
                        appointment.getPatient().getUser().getPhoneNumber(),
                        appointment.getPatient().getUser().getFirstName(),
                        appointment.getAppointmentDateTime()
                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                    );
                } catch (Exception e) {
                    // Log error and continue with next appointment
                }
            });
    }
} 