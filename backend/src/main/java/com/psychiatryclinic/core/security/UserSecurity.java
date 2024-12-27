package com.psychiatryclinic.core.security;

import com.psychiatryclinic.business.abstracts.*;
import com.psychiatryclinic.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSecurity {
    private final UserService userService;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final PaymentService paymentService;
    private final WorkingHoursService workingHoursService;

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((User) authentication.getPrincipal()).getId();
    }

    public boolean isOwner(String id) {
        String currentUserId = getCurrentUserId();
        return userService.getById(id).getId().equals(currentUserId);
    }

    public boolean isOwnerByEmail(String email) {
        String currentUserId = getCurrentUserId();
        return userService.getByEmail(email).getId().equals(currentUserId);
    }

    public boolean isDoctorOwner(String doctorId) {
        String currentUserId = getCurrentUserId();
        return doctorService.getById(doctorId).getUser().getId().equals(currentUserId);
    }

    public boolean isPatientOwner(String patientId) {
        String currentUserId = getCurrentUserId();
        return patientService.getById(patientId).getUser().getId().equals(currentUserId);
    }

    public boolean isAppointmentOwner(String appointmentId) {
        String currentUserId = getCurrentUserId();
        var appointment = appointmentService.getById(appointmentId);
        return patientService.getById(appointment.getPatientId()).getUser().getId().equals(currentUserId);
    }

    public boolean isDoctorOfAppointment(String appointmentId) {
        String currentUserId = getCurrentUserId();
        var appointment = appointmentService.getById(appointmentId);
        return doctorService.getById(appointment.getDoctorId()).getUser().getId().equals(currentUserId);
    }

    public boolean isPaymentOwner(String paymentId) {
        String currentUserId = getCurrentUserId();
        var payment = paymentService.getById(paymentId);
        return patientService.getById(payment.getPatientId()).getUser().getId().equals(currentUserId);
    }

    public boolean isDoctorOfPayment(String paymentId) {
        String currentUserId = getCurrentUserId();
        var payment = paymentService.getById(paymentId);
        return doctorService.getById(payment.getDoctorId()).getUser().getId().equals(currentUserId);
    }

    public boolean isWorkingHoursOwner(String workingHoursId) {
        String currentUserId = getCurrentUserId();
        var workingHours = workingHoursService.getById(workingHoursId);
        return doctorService.getById(workingHours.getDoctorId()).getUser().getId().equals(currentUserId);
    }
} 