package com.psychiatryclinic.config;

import com.psychiatryclinic.dataAccess.DoctorRepository;
import com.psychiatryclinic.dataAccess.PatientRepository;
import com.psychiatryclinic.dataAccess.UserRepository;
import com.psychiatryclinic.entities.Doctor;
import com.psychiatryclinic.entities.Patient;
import com.psychiatryclinic.entities.User;
import com.psychiatryclinic.entities.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@DependsOn("securityConfig")
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Debug için kullanıcı sayısını kontrol edelim
        System.out.println("Mevcut kullanıcı sayısı: " + userRepository.count());
        
        // Veritabanı boşsa örnek veriler ekle
        if (userRepository.count() == 0) {
            createSampleData();
            // Eklenen kullanıcıları kontrol edelim
            System.out.println("Örnek veriler eklendi. Yeni kullanıcı sayısı: " + userRepository.count());
        }
    }

    private void createSampleData() {
        // Admin kullanıcısı oluştur
        User adminUser = User.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@example.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .isActive(true)
                .build();
        userRepository.save(adminUser);
        
        // Debug için admin kullanıcısını kontrol edelim
        System.out.println("Admin kullanıcısı oluşturuldu: " + adminUser.getEmail());
    }
} 