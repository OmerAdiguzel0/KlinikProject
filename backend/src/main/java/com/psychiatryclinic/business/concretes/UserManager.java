package com.psychiatryclinic.business.concretes;

import com.psychiatryclinic.business.abstracts.UserService;
import com.psychiatryclinic.business.requests.user.CreateUserRequest;
import com.psychiatryclinic.business.requests.user.UpdateUserRequest;
import com.psychiatryclinic.business.responses.user.GetAllUsersResponse;
import com.psychiatryclinic.business.responses.user.GetUserResponse;
import com.psychiatryclinic.business.rules.UserBusinessRules;
import com.psychiatryclinic.core.utilities.email.EmailService;
import com.psychiatryclinic.core.utilities.mappers.ModelMapperService;
import com.psychiatryclinic.dataAccess.DoctorRepository;
import com.psychiatryclinic.dataAccess.PatientRepository;
import com.psychiatryclinic.dataAccess.UserRepository;
import com.psychiatryclinic.entities.Doctor;
import com.psychiatryclinic.entities.Patient;
import com.psychiatryclinic.entities.User;
import com.psychiatryclinic.entities.enums.Role;
import com.psychiatryclinic.core.exceptions.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserManager implements UserService {
    private UserRepository userRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private ModelMapperService modelMapperService;
    private UserBusinessRules userBusinessRules;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void add(CreateUserRequest createUserRequest) {
        userBusinessRules.checkIfEmailExists(createUserRequest.getEmail());
        
        User user = this.modelMapperService.forRequest()
                .map(createUserRequest, User.class);
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        
        userRepository.save(user);
        
        if (user.getRole() == Role.DOCTOR) {
            Doctor doctor = Doctor.builder()
                .user(user)
                .isActive(true)
                .build();
            doctorRepository.save(doctor);
        }
        
        try {
            System.out.println("Email gönderimi başlıyor..."); // Debug log
            emailService.sendWelcomeEmail(
                createUserRequest.getEmail(),
                createUserRequest.getFirstName() + " " + createUserRequest.getLastName()
            );
            System.out.println("Email gönderimi tamamlandı"); // Debug log
        } catch (Exception e) {
            System.err.println("Email gönderimi sırasında hata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void update(UpdateUserRequest updateUserRequest) {
        userBusinessRules.checkIfUserExists(updateUserRequest.getId());
        
        User user = this.modelMapperService.forRequest()
                .map(updateUserRequest, User.class);
        
        userRepository.save(user);
    }

    @Override
    public void delete(String id) {
        userBusinessRules.checkIfUserExists(id);
        userRepository.deleteById(id);
    }

    @Override
    public GetUserResponse getById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        return modelMapperService.forResponse()
                .map(user, GetUserResponse.class);
    }

    @Override
    public GetUserResponse getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        return modelMapperService.forResponse()
                .map(user, GetUserResponse.class);
    }

    @Override
    public List<GetAllUsersResponse> getAll() {
        List<User> users = userRepository.findAll();
        
        return users.stream()
                .map(user -> modelMapperService.forResponse()
                        .map(user, GetAllUsersResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public Patient getPatientById(String id) {
        return patientRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Hasta bulunamadı"));
    }

    @Override
    public Doctor getDoctorById(String id) {
        return doctorRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Doktor bulunamadı"));
    }

    @Transactional
    public void updateUserRole(String userId, Role newRole) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("Kullanıcı bulunamadı"));
        
        Role oldRole = user.getRole();
        
        // Eğer PATIENT'tan DOCTOR'a geçiş varsa
        if (oldRole == Role.PATIENT && newRole == Role.DOCTOR) {
            // Önce Patient kaydını sil
            patientRepository.deleteByUserId(userId);
            
            // Yeni Doctor kaydı oluştur
            Doctor doctor = Doctor.builder()
                .user(user)
                .specialty("")
                .licenseNumber("")
                .isActive(true)
                .build();
            doctorRepository.save(doctor);
        }
        // Eğer DOCTOR'dan PATIENT'a geçiş varsa
        else if (oldRole == Role.DOCTOR && newRole == Role.PATIENT) {
            // Önce Doctor kaydını sil
            doctorRepository.deleteByUserId(userId);
            
            // Yeni Patient kaydı oluştur
            Patient patient = new Patient();
            patient.setUser(user);
            patientRepository.save(patient);
        }
        
        // Kullanıcının rolünü güncelle
        user.setRole(newRole);
        userRepository.save(user);
    }

    @Transactional
    public void syncUserRoles() {
        List<User> allUsers = userRepository.findAll();
        
        for (User user : allUsers) {
            if (user.getRole() == Role.DOCTOR && !doctorRepository.existsByUserId(user.getId())) {
                // Doctor kaydı yoksa oluştur
                Doctor doctor = Doctor.builder()
                    .user(user)
                    .specialty("")
                    .licenseNumber("")
                    .isActive(true)
                    .build();
                doctorRepository.save(doctor);
                
                // Varsa Patient kaydını sil
                patientRepository.deleteByUserId(user.getId());
            }
            else if (user.getRole() == Role.PATIENT && !patientRepository.existsByUserId(user.getId())) {
                // Patient kaydı yoksa oluştur
                Patient patient = new Patient();
                patient.setUser(user);
                patientRepository.save(patient);
                
                // Varsa Doctor kaydını sil
                doctorRepository.deleteByUserId(user.getId());
            }
        }
    }
} 