🏥 Psikiyatri Kliniği Yönetim Sistemi

Modern psikiyatri kliniklerinin ihtiyaçlarını karşılamak için geliştirilmiş kapsamlı bir yönetim sistemi. Hasta-doktor etkileşimlerini, randevuları ve tedavi süreçlerini dijital ortamda kolayca yönetmenizi sağlar.

🌟 Öne Çıkan Özellikler

🔐 Kullanıcı Yönetimi ve Güvenlik

Rol tabanlı yetkilendirme (RBAC): Hasta, Doktor ve Admin rolleri

JWT tabanlı kimlik doğrulama

Google OAuth2.0 entegrasyonu

Güvenli şifre yönetimi ve sıfırlama

👨‍⚕️ Doktor Modülü

Hasta randevularını yönetme

Çalışma saatlerini düzenleme

Hasta geçmişi ve tedavi notları

Uzmanlık alanı ve sertifika yönetimi

👤 Hasta Modülü

Online randevu alma ve yönetme

Doktor profillerini inceleme

Tedavi geçmişini takip etme

Fatura ve ödeme işlemleri

📨 Bildirim Sistemi

Otomatik email bildirimleri

Randevu hatırlatmaları

WhatsApp entegrasyonu

Kendi bildirim şablonlarını oluşturma

🛠 Teknoloji Yığını (Tech Stack)

Backend

Java 17

Spring Boot 3.2.3

Spring Security

JWT Authentication

MongoDB

Maven

Veritabanı

MongoDB Atlas

Spring Data MongoDB

Email ve Mesajlaşma

Jakarta Mail

Gmail SMTP

Twilio WhatsApp API

Güvenlik

Spring Security

JWT Tokens

Google OAuth2.0

BCrypt şifreleme

💻 Kurulum

Ön Gereksinimler

Java 17 veya üzeri

Maven

MongoDB

Gmail hesabı (SMTP için)

Twilio hesabı (WhatsApp bildirimleri için)

Kurulum Adımları

Projeyi klonlayın:

git clone https://github.com/your-username/psychiatry-clinic.git
cd psychiatry-clinic

application-example.properties dosyasını application.properties olarak kopyalayın:

cp backend/src/main/resources/application-example.properties backend/src/main/resources/application.properties

application.properties dosyasını kendi bilgilerinizle güncelleyin:

MongoDB bağlantı bilgileri

Mail sunucusu ayarları

OAuth2 client bilgileri

Twilio API anahtarları

Projeyi derleyin:

cd backend
mvn clean install

Uygulamayı başlatın:

mvn spring-boot:run

📚 API Dokümantasyonu

Swagger UI: http://localhost:8080/swagger-ui.html

Temel Endpointler

🛡️ Kimlik Doğrulama

POST /api/v1/auth/register: Yeni kullanıcı kaydı

POST /api/v1/auth/login: Kullanıcı girişi

POST /api/v1/auth/refresh-token: Token yenileme

📅 Randevu İşlemleri

POST /api/v1/appointments: Yeni randevu oluşturma

GET /api/v1/appointments: Randevuları listeleme

PUT /api/v1/appointments/{id}: Randevu güncelleme

👨‍⚕️ Doktor İşlemleri

GET /api/v1/doctors: Doktor listesi

GET /api/v1/doctors/{id}/schedule: Doktor programı

PUT /api/v1/doctors/{id}/availability: Müsaitlik durumu güncelleme

🔒 Güvenlik Önlemleri

Tüm şifreler BCrypt ile hashlenir

JWT token'ları 24 saat geçerlidir

Hassas bilgiler environment variable'lar ile yönetilir

CORS politikaları düzenlenmiştir

Rate limiting uygulanmıştır

🤝 Katkıda Bulunma

Projeyi fork'layın.

Yeni bir branch oluşturun (git checkout -b feature/yeni-ozellik).

Değişikliklerinizi commit edin (git commit -m 'feat: Yeni bir özellik ekle').

Branch'inizi push edin (git push origin feature/yeni-ozellik).

Pull Request oluşturun.

📝 Lisans

Bu proje MIT lisansı altında lisanslanmıştır.

