package com.psychiatryclinic.business.rules;

import com.psychiatryclinic.dataAccess.DoctorRepository;
import com.psychiatryclinic.dataAccess.PatientRepository;
import com.psychiatryclinic.dataAccess.TreatmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TreatmentBusinessRules {
    private TreatmentRepository treatmentRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;

    public void checkIfTreatmentExists(String id) {
        if (!treatmentRepository.existsById(id)) {
            throw new RuntimeException("Tedavi bulunamadı");
        }
    }

    public void checkIfDoctorExists(String doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new RuntimeException("Doktor bulunamadı");
        }
    }

    public void checkIfPatientExists(String patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new RuntimeException("Hasta bulunamadı");
        }
    }

    public void checkIfEndDateValid(String id) {
        treatmentRepository.findById(id).ifPresent(treatment -> {
            if (treatment.getEndDate() != null && 
                treatment.getEndDate().isBefore(treatment.getStartDate())) {
                throw new RuntimeException("Bitiş tarihi başlangıç tarihinden önce olamaz");
            }
        });
    }
} 