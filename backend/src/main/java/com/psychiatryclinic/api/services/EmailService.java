package com.psychiatryclinic.api.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendWelcomeEmail(String toEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Psikiyatri Klinigi - Hosgeldiniz!");
            
            String content = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                    <h2>Psikiyatri Klinigine Hosgeldiniz!</h2>
                    <p>Merhaba,</p>
                    <p>Psikiyatri Klinigi ailesine katildiginiz icin tesekkur ederiz.</p>
                    <p>Sistemimizde yapabilecekleriniz:</p>
                    <ul>
                        <li>Online randevu alabilirsiniz</li>
                        <li>Doktorlarimizla iletisime gecebilirsiniz</li>
                        <li>Randevu gecmisinizi goruntuleyebilirsiniz</li>
                        <li>Kisisel bilgilerinizi guncelleyebilirsiniz</li>
                    </ul>
                    <p style="margin-top: 20px;">Sorulariniz icin bize ulasabilirsiniz:</p>
                    <p>Email: info@psikiyatriklinik.com</p>
                    <p>Tel: +90 (XXX) XXX XX XX</p>
                    <hr style="border: 1px solid #eee; margin: 20px 0;">
                    <p style="color: #666; font-size: 14px;">
                        Bu mail otomatik olarak gonderilmistir. Lutfen bu maili yanitlamayiniz.
                    </p>
                    <p style="color: #666; font-size: 14px;">
                        Saygilarimizla,<br>
                        Psikiyatri Klinigi Ekibi
                    </p>
                </div>
            """;
            
            helper.setText(content, true);
            mailSender.send(message);
            
        } catch (MessagingException e) {
            throw new RuntimeException("Mail gonderimi sirasinda bir hata olustu", e);
        }
    }
} 