package com.psychiatryclinic.business.rules;

import com.psychiatryclinic.core.exceptions.BusinessException;
import com.psychiatryclinic.business.constants.Messages;
import com.psychiatryclinic.dataAccess.PatientRepository;
import com.psychiatryclinic.dataAccess.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PatientBusinessRules {
    private PatientRepository patientRepository;
    private UserRepository userRepository;

    public void checkIfPatientExists(String id) {
        if (!patientRepository.existsById(id)) {
            throw new BusinessException(Messages.Patient.NOT_EXISTS);
        }
    }

    public void checkIfUserExists(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(Messages.User.NOT_EXISTS);
        }
    }
} 