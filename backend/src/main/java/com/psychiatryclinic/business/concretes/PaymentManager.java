package com.psychiatryclinic.business.concretes;

import com.psychiatryclinic.business.abstracts.InvoiceService;
import com.psychiatryclinic.business.abstracts.PaymentService;
import com.psychiatryclinic.business.requests.invoice.CreateInvoiceRequest;
import com.psychiatryclinic.business.requests.payment.CreatePaymentRequest;
import com.psychiatryclinic.business.responses.payment.GetPaymentResponse;
import com.psychiatryclinic.business.rules.PaymentBusinessRules;
import com.psychiatryclinic.core.exceptions.BusinessException;
import com.psychiatryclinic.core.utilities.email.EmailService;
import com.psychiatryclinic.core.utilities.mappers.ModelMapperService;
import com.psychiatryclinic.dataAccess.AppointmentRepository;
import com.psychiatryclinic.dataAccess.InvoiceRepository;
import com.psychiatryclinic.dataAccess.AppointmentRepository;
import com.psychiatryclinic.dataAccess.PaymentRepository;
import com.psychiatryclinic.dataAccess.PatientRepository;
import com.psychiatryclinic.entities.Appointment;
import com.psychiatryclinic.entities.Patient;
import com.psychiatryclinic.entities.Payment;
import com.psychiatryclinic.entities.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PaymentManager implements PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentManager.class);

    private PaymentRepository paymentRepository;
    private ModelMapperService modelMapperService;
    private PaymentBusinessRules rules;
    private EmailService emailService;
    private InvoiceService invoiceService;
    private PatientRepository patientRepository;

    @Override
    public void add(CreatePaymentRequest createPaymentRequest) {
        rules.checkIfAppointmentExists(createPaymentRequest.getAppointmentId());
        rules.checkIfAmountValid(createPaymentRequest.getAmount());
        rules.checkIfAppointmentHasPayment(createPaymentRequest.getAppointmentId());

        Appointment appointment = rules.getAppointmentById(createPaymentRequest.getAppointmentId());
        Patient patient = patientRepository.findPatientWithUser(appointment.getPatientId());
        if (patient == null || patient.getUser() == null || patient.getUser().getEmail() == null) {
            throw new BusinessException("Hasta email bilgisi bulunamadı");
        }

        logger.debug("Patient Email: {}", patient.getUser().getEmail());

        Payment payment = Payment.builder()
            .appointment(appointment)
            .patientId(appointment.getPatientId())
            .doctorId(appointment.getDoctorId())
            .amount(createPaymentRequest.getAmount())
            .type(createPaymentRequest.getType())
            .status(PaymentStatus.PENDING)
            .paymentDate(LocalDateTime.now())
            .transactionId(UUID.randomUUID().toString())
            .notes(createPaymentRequest.getNotes())
            .build();

        payment = paymentRepository.save(payment);

        emailService.sendPaymentConfirmation(
            patient.getUser().getEmail(),
            String.format(
                "Ödeme Tutarı: %.2f TL\nÖdeme Tipi: %s\nİşlem No: %s",
                payment.getAmount(),
                payment.getType(),
                payment.getTransactionId()
            )
        );

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            createInvoiceForPayment(payment);
            logger.info("Ödeme için fatura oluşturuldu. Ödeme ID: {}", payment.getId());
        }
    }

    @Override
    public void updateStatus(String id, PaymentStatus status) {
        rules.checkIfPaymentExists(id);

        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Ödeme bulunamadı"));
        
        Patient patient = patientRepository.findById(payment.getPatientId())
            .orElseThrow(() -> new BusinessException("Hasta bulunamadı"));

        payment.setStatus(status);
        payment = paymentRepository.save(payment);

        emailService.sendPaymentStatusUpdate(
            patient.getUser().getEmail(),
            status,
            String.format(
                "Ödeme Tutarı: %.2f TL\nÖdeme Tipi: %s\nİşlem No: %s",
                payment.getAmount(),
                payment.getType(),
                payment.getTransactionId()
            )
        );

        if (status == PaymentStatus.COMPLETED) {
            createInvoiceForPayment(payment);
            logger.info("Tamamlanan ödeme için fatura oluşturuldu. Ödeme ID: {}", payment.getId());
        }
    }

    @Override
    public GetPaymentResponse getById(String id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ödeme bulunamadı"));

        return modelMapperService.forResponse()
            .map(payment, GetPaymentResponse.class);
    }

    @Override
    public List<GetPaymentResponse> getByAppointmentId(String appointmentId) {
        List<Payment> payments = paymentRepository.findByAppointment_Id(appointmentId);

        return payments.stream()
            .map(payment -> modelMapperService.forResponse()
                .map(payment, GetPaymentResponse.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<GetPaymentResponse> getByPatientId(String patientId) {
        List<Payment> payments = paymentRepository.findByPatientId(patientId);
        return payments.stream()
            .map(payment -> modelMapperService.forResponse()
                .map(payment, GetPaymentResponse.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<GetPaymentResponse> getByDoctorId(String doctorId) {
        List<Payment> payments = paymentRepository.findByDoctorId(doctorId);
        return payments.stream()
            .map(payment -> modelMapperService.forResponse()
                .map(payment, GetPaymentResponse.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<GetPaymentResponse> getByStatus(PaymentStatus status) {
        List<Payment> payments = paymentRepository.findByStatus(status);
        return payments.stream()
            .map(payment -> modelMapperService.forResponse()
                .map(payment, GetPaymentResponse.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<GetPaymentResponse> getByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Payment> payments = paymentRepository.findByPaymentDateBetween(start, end);
        return payments.stream()
            .map(payment -> modelMapperService.forResponse()
                .map(payment, GetPaymentResponse.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<GetPaymentResponse> getAll() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
            .map(payment -> modelMapperService.forResponse()
                .map(payment, GetPaymentResponse.class))
            .collect(Collectors.toList());
    }

    private void createInvoiceForPayment(Payment payment) {
        CreateInvoiceRequest request = new CreateInvoiceRequest();
        request.setPatientId(payment.getPatientId());
        request.setAppointmentId(payment.getAppointment().getId());
        request.setAmount(payment.getAmount());
        request.setDescription("Muayene ücreti");
        request.setDueDate(LocalDateTime.now().plusDays(30));
        
        invoiceService.add(request);
    }

    // ... diğer metodların implementasyonları
} 