package com.psychiatryclinic.core.utilities.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);
    private static final String TEMPLATE_SID = "HX1ab37d77caae8e980b808ad321344d85";

    @Value("${twilio.account-sid}")
    private String ACCOUNT_SID;

    @Value("${twilio.auth-token}")
    private String AUTH_TOKEN;

    @Value("${twilio.phone-number}")
    private String FROM_NUMBER;

    public void sendAppointmentReminder(String toPhoneNumber, String patientName, String appointmentDateTime) {
        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            
            String whatsappNumber = "whatsapp:" + toPhoneNumber;

            Message message = Message.creator(
                new PhoneNumber(whatsappNumber),
                new PhoneNumber(FROM_NUMBER),
                String.format("Sayın %s. %s tarihli randevunuz için bir uyarı mesajıdır. Lütfen en az 15 dakika önceden burada olunuz.", 
                    patientName, appointmentDateTime)
            ).create();

            logger.info("WhatsApp mesajı gönderildi. SID: {}", message.getSid());
        } catch (Exception e) {
            logger.error("WhatsApp mesajı gönderilirken hata oluştu: {}", e.getMessage());
        }
    }
} 