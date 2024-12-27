package com.psychiatryclinic.business.concretes;

import com.psychiatryclinic.dataAccess.DoctorRepository;
import com.psychiatryclinic.dataAccess.PatientRepository;
import com.psychiatryclinic.entities.User;
import com.psychiatryclinic.entities.enums.Role;
import com.psychiatryclinic.core.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthManager {
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    private void validateUserRole(User user) {
        if (user.getRole() == Role.DOCTOR && !doctorRepository.existsByUserId(user.getId())) {
            throw new BusinessException("Doktor kaydı bulunamadı. Lütfen sistem yöneticisi ile iletişime geçin.");
        }
        if (user.getRole() == Role.PATIENT && !patientRepository.existsByUserId(user.getId())) {
            throw new BusinessException("Hasta kaydı bulunamadı. Lütfen sistem yöneticisi ile iletişime geçin.");
        }
    }
} 