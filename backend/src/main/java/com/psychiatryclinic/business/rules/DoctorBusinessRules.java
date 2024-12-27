package com.psychiatryclinic.business.rules;

import com.psychiatryclinic.core.exceptions.BusinessException;
import com.psychiatryclinic.business.constants.Messages;
import com.psychiatryclinic.dataAccess.DoctorRepository;
import com.psychiatryclinic.dataAccess.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DoctorBusinessRules {
    private DoctorRepository doctorRepository;
    private UserRepository userRepository;

    public void checkIfDoctorExists(String id) {
        if (!doctorRepository.existsById(id)) {
            throw new BusinessException(Messages.Doctor.NOT_EXISTS);
        }
    }

    public void checkIfUserExists(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(Messages.User.NOT_EXISTS);
        }
    }

    public void checkIfLicenseNumberExists(String licenseNumber) {
        if (doctorRepository.existsByLicenseNumber(licenseNumber)) {
            throw new BusinessException(Messages.Doctor.ALREADY_EXISTS);
        }
    }
} 