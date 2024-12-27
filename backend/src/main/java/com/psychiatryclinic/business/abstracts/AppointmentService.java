package com.psychiatryclinic.business.abstracts;

import com.psychiatryclinic.business.requests.appointment.CreateAppointmentRequest;
import com.psychiatryclinic.business.requests.appointment.UpdateAppointmentRequest;
import com.psychiatryclinic.business.responses.appointment.GetAllAppointmentsResponse;
import com.psychiatryclinic.business.responses.appointment.GetAppointmentResponse;
import com.psychiatryclinic.entities.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
    void add(CreateAppointmentRequest createAppointmentRequest);
    void update(UpdateAppointmentRequest updateAppointmentRequest);
    void delete(String id);
    void updateStatus(String id, AppointmentStatus status);
    GetAppointmentResponse getById(String id);
    List<GetAllAppointmentsResponse> getAll();
    List<GetAppointmentResponse> getByPatientId(String patientId);
    List<GetAppointmentResponse> getByDoctorId(String doctorId);
    List<GetAppointmentResponse> getByDateRange(LocalDateTime start, LocalDateTime end);
} 