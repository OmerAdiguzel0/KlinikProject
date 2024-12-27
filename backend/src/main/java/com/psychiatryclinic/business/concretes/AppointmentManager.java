package com.psychiatryclinic.business.concretes;

import com.psychiatryclinic.business.abstracts.AppointmentService;
import com.psychiatryclinic.business.requests.appointment.CreateAppointmentRequest;
import com.psychiatryclinic.business.requests.appointment.UpdateAppointmentRequest;
import com.psychiatryclinic.business.responses.appointment.GetAllAppointmentsResponse;
import com.psychiatryclinic.business.responses.appointment.GetAppointmentResponse;
import com.psychiatryclinic.business.rules.AppointmentBusinessRules;
import com.psychiatryclinic.core.utilities.mappers.ModelMapperService;
import com.psychiatryclinic.dataAccess.AppointmentRepository;
import com.psychiatryclinic.dataAccess.DoctorRepository;
import com.psychiatryclinic.dataAccess.PatientRepository;
import com.psychiatryclinic.entities.Appointment;
import com.psychiatryclinic.entities.Doctor;
import com.psychiatryclinic.entities.Patient;
import com.psychiatryclinic.entities.enums.AppointmentStatus;
import com.psychiatryclinic.core.utilities.email.EmailService;
import com.psychiatryclinic.core.exceptions.BusinessException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AppointmentManager implements AppointmentService {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentManager.class);

    private AppointmentRepository appointmentRepository;
    private ModelMapperService modelMapperService;
    private AppointmentBusinessRules rules;
    private EmailService emailService;
    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;

    @Override
    public void add(CreateAppointmentRequest createAppointmentRequest) {
        rules.checkIfDoctorExists(createAppointmentRequest.getDoctorId());
        rules.checkIfPatientExists(createAppointmentRequest.getPatientId());
        rules.checkIfAppointmentTimeValid(createAppointmentRequest.getAppointmentDateTime());
        
        rules.checkIfDoctorAvailableForDay(
            createAppointmentRequest.getDoctorId(), 
            createAppointmentRequest.getAppointmentDateTime()
        );
        rules.checkIfSlotAvailable(
            createAppointmentRequest.getDoctorId(), 
            createAppointmentRequest.getAppointmentDateTime()
        );
        
        Appointment appointment = this.modelMapperService.forRequest()
                .map(createAppointmentRequest, Appointment.class);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        
        Patient patient = patientRepository.findById(createAppointmentRequest.getPatientId())
            .orElseThrow(() -> new BusinessException("Hasta bulunamadı"));
        Doctor doctor = doctorRepository.findById(createAppointmentRequest.getDoctorId())
            .orElseThrow(() -> new BusinessException("Doktor bulunamadı"));

        appointmentRepository.save(appointment);

        try {
            emailService.sendAppointmentConfirmation(
                patient.getUser().getEmail(),
                String.format(
                    """
                    Randevu Tarihi: %s
                    Doktor: %s Dr. %s %s
                    Notlar: %s
                    """,
                    appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    doctor.getTitle() != null ? doctor.getTitle() : "",
                    doctor.getUser().getFirstName(),
                    doctor.getUser().getLastName(),
                    appointment.getNotes() != null ? appointment.getNotes() : ""
                )
            );
        } catch (Exception e) {
            logger.error("Email gönderimi başarısız oldu: {}", e.getMessage());
        }
    }

    @Override
    public void update(UpdateAppointmentRequest updateAppointmentRequest) {
        rules.checkIfAppointmentExists(updateAppointmentRequest.getId());
        
        Appointment appointment = this.modelMapperService.forRequest()
                .map(updateAppointmentRequest, Appointment.class);
        
        appointmentRepository.save(appointment);
    }

    @Override
    public void delete(String id) {
        rules.checkIfAppointmentExists(id);
        appointmentRepository.deleteById(id);
    }

    @Override
    public void updateStatus(String id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Randevu bulunamadı"));
        
        appointment.setStatus(status);
        appointmentRepository.save(appointment);
        
        if (status == AppointmentStatus.CANCELLED) {
            emailService.sendAppointmentCancellation(
                appointment.getPatientId(),
                String.format(
                    "İptal edilen randevu:\nTarih: %s\nSaat: %s",
                    appointment.getAppointmentDateTime().toLocalDate(),
                    appointment.getAppointmentDateTime().toLocalTime()
                )
            );
        }
    }

    @Override
    public GetAppointmentResponse getById(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı"));
        
        return modelMapperService.forResponse()
                .map(appointment, GetAppointmentResponse.class);
    }

    @Override
    public List<GetAllAppointmentsResponse> getAll() {
        List<Appointment> appointments = appointmentRepository.findAll();
        
        return appointments.stream()
                .map(appointment -> modelMapperService.forResponse()
                        .map(appointment, GetAllAppointmentsResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<GetAppointmentResponse> getByPatientId(String patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        
        return appointments.stream()
                .map(appointment -> modelMapperService.forResponse()
                        .map(appointment, GetAppointmentResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<GetAppointmentResponse> getByDoctorId(String doctorId) {
        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId);
        
        return appointments.stream()
                .map(appointment -> modelMapperService.forResponse()
                        .map(appointment, GetAppointmentResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<GetAppointmentResponse> getByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Appointment> appointments = appointmentRepository.findByAppointmentDateTimeBetween(start, end);
        
        return appointments.stream()
                .map(appointment -> modelMapperService.forResponse()
                        .map(appointment, GetAppointmentResponse.class))
                .collect(Collectors.toList());
    }
} 