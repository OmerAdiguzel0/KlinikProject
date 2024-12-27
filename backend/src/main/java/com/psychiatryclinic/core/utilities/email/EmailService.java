package com.psychiatryclinic.core.utilities.email;

import com.psychiatryclinic.entities.enums.PaymentStatus;
import java.time.LocalDateTime;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);
    void sendHtmlMessage(String to, String subject, String htmlContent);
    void sendWelcomeEmail(String to, String name);
    void sendPasswordResetEmail(String to, String resetToken);
    void sendAppointmentConfirmation(String email, String appointmentDetails);
    void sendAppointmentDayReminder(String patientId, String doctorId, LocalDateTime appointmentDateTime);
    void sendAppointmentHourReminder(String patientId, String doctorId, LocalDateTime appointmentDateTime);
    void sendAppointmentCancellation(String patientId, String appointmentDetails);
    void sendPaymentConfirmation(String email, String paymentDetails);
    void sendPaymentStatusUpdate(String email, PaymentStatus status, String paymentDetails);
    void sendInvoiceEmail(String email, String invoiceNumber, byte[] pdfContent);
} 