package com.psychiatryclinic.core.utilities.email;

import com.psychiatryclinic.core.exceptions.BusinessException;
import com.psychiatryclinic.dataAccess.DoctorRepository;
import com.psychiatryclinic.dataAccess.PatientRepository;
import com.psychiatryclinic.entities.Doctor;
import com.psychiatryclinic.entities.Patient;
import com.psychiatryclinic.entities.enums.PaymentStatus;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import jakarta.mail.internet.InternetAddress;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    
    @Value("${app.mail.sender.email}")
    private String senderEmail;
    
    @Value("${app.mail.sender.name}")
    private String senderName;

    private final JavaMailSender emailSender;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ResourceLoader resourceLoader;

    @PostConstruct
    public void init() {
        System.out.println("EmailServiceImpl initialized");
        System.out.println("JavaMailSender instance: " + emailSender);
        System.out.println("Sender Email: " + senderEmail);
        System.out.println("Sender Name: " + senderName);
    }

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(new InternetAddress(senderEmail, "Psikiyatri Kliniği", "UTF-8"));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            
            emailSender.send(message);
        } catch (Exception e) {
            logger.error("Email gönderimi sırasında hata oluştu: {}", e.getMessage(), e);
            throw new RuntimeException("Email gönderilemedi: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendHtmlMessage(String to, String subject, String htmlContent) {
        try {
            logger.info("Email gönderimi başlatılıyor. Alıcı: {}, Konu: {}", to, subject);
            
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(new InternetAddress(senderEmail, "Psikiyatri Kliniği", "UTF-8"));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            emailSender.send(message);
            logger.info("Email başarıyla gönderildi. Alıcı: {}", to);
        } catch (Exception e) {
            logger.error("Email gönderimi sırasında hata oluştu: {}", e.getMessage(), e);
            throw new RuntimeException("Email gönderilemedi: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendWelcomeEmail(String to, String name) {
        String subject = "Psikiyatri Kliniğine Hoş Geldiniz";
        String htmlContent = String.format("""
            <html>
                <body>
                    <h2>Hoş Geldiniz %s</h2>
                    <p>Psikiyatri Kliniği sistemine kaydınız başarıyla tamamlanmıştır.</p>
                    <p>Randevu almak için sisteme giriş yapabilirsiniz.</p>
                    <br>
                    <p>Saygılarımızla,</p>
                    <p>Psikiyatri Kliniği Ekibi</p>
                </body>
            </html>
            """, name);
        
        sendHtmlMessage(to, subject, htmlContent);
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        String subject = "Şifre Sıfırlama İsteği";
        String htmlContent = String.format("""
            <html>
                <body>
                    <h2>Şifre Sıfırlama</h2>
                    <p>Şifrenizi sıfırlamak için aşağıdaki linke tıklayınız:</p>
                    <a href="http://localhost:3000/reset-password?token=%s">Şifremi Sıfırla</a>
                    <p>Bu link 30 dakika süreyle geçerlidir.</p>
                    <br>
                    <p>Eğer bu isteği siz yapmadıysanız, bu emaili görmezden gelebilirsiniz.</p>
                </body>
            </html>
            """, resetToken);
        
        sendHtmlMessage(to, subject, htmlContent);
    }

    @Override
    public void sendAppointmentConfirmation(String to, String appointmentDetails) {
        String subject = "Randevu Onayı";
        String htmlContent = String.format("""
            <html>
                <body>
                    <h2>Randevunuz Onaylandı</h2>
                    <p>Randevu detaylarınız:</p>
                    <div style="background-color: #f5f5f5; padding: 15px; border-radius: 5px;">
                        %s
                    </div>
                    <p>Randevunuzu iptal etmek veya değiştirmek için sisteme giriş yapabilirsiniz.</p>
                </body>
            </html>
            """, appointmentDetails);
        
        sendHtmlMessage(to, subject, htmlContent);
    }

    @Override
    public void sendAppointmentDayReminder(String patientId, String doctorId, LocalDateTime appointmentDateTime) {
        // Hasta ve doktor bilgilerini getir
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new BusinessException("Hasta bulunamadı"));
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new BusinessException("Doktor bulunamadı"));

        String subject = "Randevu Hatırlatması - 24 Saat Kaldı";
        String htmlContent = String.format("""
            <html>
                <body>
                    <h2>Randevu Hatırlatması</h2>
                    <p>Sayın %s,</p>
                    <p>%s tarihinde Dr. %s ile randevunuza 24 saat kalmıştır.</p>
                    <br>
                    <p>Saygılarımızla,</p>
                    <p>Psikiyatri Kliniği</p>
                </body>
            </html>
            """, 
            patient.getUser().getFirstName() + " " + patient.getUser().getLastName(),
            appointmentDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
            doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName()
        );

        sendHtmlMessage(patient.getUser().getEmail(), subject, htmlContent);
    }

    @Override
    public void sendAppointmentHourReminder(String patientId, String doctorId, LocalDateTime appointmentDateTime) {
        // Hasta ve doktor bilgilerini getir
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new BusinessException("Hasta bulunamadı"));
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new BusinessException("Doktor bulunamadı"));

        String subject = "Randevu Hatırlatması - 1 Saat Kaldı";
        String htmlContent = String.format("""
            <html>
                <body>
                    <h2>Randevu Hatırlatması</h2>
                    <p>Sayın %s,</p>
                    <p>%s tarihinde Dr. %s ile randevunuza 1 saat kalmıştır.</p>
                    <br>
                    <p>Saygılarımızla,</p>
                    <p>Psikiyatri Kliniği</p>
                </body>
            </html>
            """, 
            patient.getUser().getFirstName() + " " + patient.getUser().getLastName(),
            appointmentDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
            doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName()
        );

        sendHtmlMessage(patient.getUser().getEmail(), subject, htmlContent);
    }

    @Override
    public void sendAppointmentCancellation(String patientId, String appointmentDetails) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new BusinessException("Hasta bulunamadı"));

        String subject = "Randevu İptali";
        String htmlContent = String.format("""
            <html>
                <body>
                    <h2>Randevu İptali</h2>
                    <p>Sayın %s,</p>
                    <p>Aşağıdaki randevunuz iptal edilmiştir:</p>
                    <div style="background-color: #f5f5f5; padding: 15px; border-radius: 5px;">
                        %s
                    </div>
                    <br>
                    <p>Yeni bir randevu almak için sisteme giriş yapabilirsiniz.</p>
                    <p>Saygılarımızla,</p>
                    <p>Psikiyatri Kliniği</p>
                </body>
            </html>
            """, 
            patient.getUser().getFirstName() + " " + patient.getUser().getLastName(),
            appointmentDetails
        );

        sendHtmlMessage(patient.getUser().getEmail(), subject, htmlContent);
    }

    @Override
    public void sendPaymentConfirmation(String patientId, String paymentDetails) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new BusinessException("Hasta bulunamadı"));

        String subject = "Ödeme Onayı";
        String htmlContent = String.format("""
            <html>
                <body>
                    <h2>Ödeme Onayı</h2>
                    <p>Sayın %s,</p>
                    <p>Ödemeniz başarıyla alınmıştır.</p>
                    <div style="background-color: #f5f5f5; padding: 15px; border-radius: 5px;">
                        %s
                    </div>
                    <br>
                    <p>Saygılarımızla,</p>
                    <p>Psikiyatri Kliniği</p>
                </body>
            </html>
            """, 
            patient.getUser().getFirstName() + " " + patient.getUser().getLastName(),
            paymentDetails
        );

        sendHtmlMessage(patient.getUser().getEmail(), subject, htmlContent);
    }

    @Override
    public void sendPaymentStatusUpdate(String patientId, PaymentStatus status, String paymentDetails) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new BusinessException("Hasta bulunamadı"));

        String subject = "Ödeme Durumu Güncellendi";
        String htmlContent = String.format("""
            <html>
                <body>
                    <h2>Ödeme Durumu Güncellendi</h2>
                    <p>Sayın %s,</p>
                    <p>Ödemenizin durumu <strong>%s</strong> olarak güncellenmiştir.</p>
                    <div style="background-color: #f5f5f5; padding: 15px; border-radius: 5px;">
                        %s
                    </div>
                    <br>
                    <p>Saygılarımızla,</p>
                    <p>Psikiyatri Kliniği</p>
                </body>
            </html>
            """, 
            patient.getUser().getFirstName() + " " + patient.getUser().getLastName(),
            status,
            paymentDetails
        );

        sendHtmlMessage(patient.getUser().getEmail(), subject, htmlContent);
    }

    @Override
    public void sendInvoiceEmail(String patientId, String invoiceNumber, byte[] pdfContent) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new BusinessException("Hasta bulunamadı"));

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(new InternetAddress(senderEmail, "Psikiyatri Kliniği", "UTF-8"));
            helper.setTo(patient.getUser().getEmail());
            helper.setSubject("Fatura - " + invoiceNumber);
            
            String htmlContent = String.format("""
                <html>
                    <body>
                        <h2>Fatura</h2>
                        <p>Sayın %s,</p>
                        <p>%s numaralı faturanız ekte yer almaktadır.</p>
                        <br>
                        <p>Saygılarımızla,</p>
                        <p>Psikiyatri Kliniği</p>
                    </body>
                </html>
                """, 
                patient.getUser().getFirstName() + " " + patient.getUser().getLastName(),
                invoiceNumber
            );
            
            helper.setText(htmlContent, true);
            helper.addAttachment("fatura-" + invoiceNumber + ".pdf", 
                new ByteArrayResource(pdfContent));
            
            emailSender.send(message);
            logger.info("Fatura email'i gönderildi. Hasta ID: {}, Fatura No: {}", 
                patientId, invoiceNumber);
        } catch (Exception e) {
            logger.error("Fatura email'i gönderilemedi: {}", e.getMessage());
            throw new RuntimeException("Fatura email'i gönderilemedi", e);
        }
    }
} 