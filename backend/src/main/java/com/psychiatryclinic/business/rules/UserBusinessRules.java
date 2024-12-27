package com.psychiatryclinic.business.rules;

import com.psychiatryclinic.dataAccess.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserBusinessRules {
    private UserRepository userRepository;

    public void checkIfEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Bu email adresi zaten kayıtlı");
        }
    }

    public void checkIfUserExists(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Kullanıcı bulunamadı");
        }
    }
} 