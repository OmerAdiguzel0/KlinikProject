package com.psychiatryclinic.business.rules;

import com.psychiatryclinic.core.exceptions.BusinessException;
import com.psychiatryclinic.dataAccess.AppointmentRepository;
import com.psychiatryclinic.dataAccess.PaymentRepository;
import com.psychiatryclinic.entities.Appointment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class PaymentBusinessRules {
    private PaymentRepository paymentRepository;
    private AppointmentRepository appointmentRepository;

    public void checkIfPaymentExists(String id) {
        if (!paymentRepository.existsById(id)) {
            throw new BusinessException("Ödeme kaydı bulunamadı");
        }
    }

    public void checkIfAppointmentExists(String appointmentId) {
        if (!appointmentRepository.existsById(appointmentId)) {
            throw new BusinessException("Randevu bulunamadı");
        }
    }

    public void checkIfAmountValid(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Ödeme tutarı sıfırdan büyük olmalıdır");
        }
    }

    public Appointment getAppointmentById(String appointmentId) {
        return appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new BusinessException("Randevu bulunamadı"));
    }

    public void checkIfAppointmentHasPayment(String appointmentId) {
        if (!paymentRepository.findByAppointment_Id(appointmentId).isEmpty()) {
            throw new BusinessException("Bu randevu için zaten ödeme yapılmış");
        }
    }
} 