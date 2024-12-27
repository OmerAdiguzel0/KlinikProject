package com.psychiatryclinic.business.constants;

public class Messages {
    public static class Doctor {
        public static final String NOT_EXISTS = "Doktor bulunamadı";
        public static final String ALREADY_EXISTS = "Bu email ile kayıtlı bir doktor zaten var";
        public static final String NOT_AVAILABLE = "Doktor müsait değil";
    }

    public static class Patient {
        public static final String NOT_EXISTS = "Hasta bulunamadı";
        public static final String ALREADY_EXISTS = "Bu email ile kayıtlı bir hasta zaten var";
    }

    public static class Appointment {
        public static final String NOT_EXISTS = "Randevu bulunamadı";
        public static final String DOCTOR_NOT_AVAILABLE = "Doktor bu saatte müsait değil";
        public static final String PATIENT_HAS_APPOINTMENT = "Hastanın bu saatte başka bir randevusu var";
        public static final String INVALID_TIME = "Geçmiş bir tarihe randevu oluşturulamaz";
        public static final String ALREADY_CANCELLED = "Randevu zaten iptal edilmiş";
        public static final String ALREADY_COMPLETED = "Randevu zaten tamamlanmış";
    }

    public static class Treatment {
        public static final String NOT_EXISTS = "Tedavi bulunamadı";
        public static final String ALREADY_COMPLETED = "Tedavi zaten tamamlanmış";
        public static final String ALREADY_DISCONTINUED = "Tedavi zaten sonlandırılmış";
    }

    public static class User {
        public static final String NOT_EXISTS = "Kullanıcı bulunamadı";
        public static final String ALREADY_EXISTS = "Bu email ile kayıtlı bir kullanıcı zaten var";
        public static final String INVALID_CREDENTIALS = "Email veya şifre hatalı";
        public static final String NOT_ACTIVE = "Hesabınız aktif değil";
    }
} 