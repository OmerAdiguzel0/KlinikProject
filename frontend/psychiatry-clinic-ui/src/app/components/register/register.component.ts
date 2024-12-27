import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  registerData = {
    firstName: '',
    lastName: '',
    email: '',
    password: ''
  };

  loading = false;
  isGoogleLoading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private messageService: MessageService
  ) {}

  // Şifre kontrol metodları
  hasMinLength(): boolean {
    return this.registerData.password.length >= 8;
  }

  hasUpperCase(): boolean {
    return /[A-Z]/.test(this.registerData.password);
  }

  hasLowerCase(): boolean {
    return /[a-z]/.test(this.registerData.password);
  }

  hasNumber(): boolean {
    return /[0-9]/.test(this.registerData.password);
  }

  hasSpecialChar(): boolean {
    return /[@$!%*?&]/.test(this.registerData.password);
  }

  onSubmit() {
    if (this.loading) return;

    console.log('🎯 Register form submit edildi');
    console.log('📝 Form verileri:', this.registerData);
    
    this.loading = true;
    this.errorMessage = '';

    this.authService.register(this.registerData).subscribe({
      next: (response) => {
        console.log('✨ Component - Register başarılı:', response);
        this.messageService.add({
          severity: 'success',
          summary: 'Başarılı',
          detail: 'Kayıt işlemi başarıyla tamamlandı!'
        });
        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.error('💥 Component - Register hatası:', {
          status: error.status,
          statusText: error.statusText,
          error: error.error,
          message: error.message
        });
        
        this.errorMessage = error.error?.message || 'Kayıt işlemi sırasında bir hata oluştu.';
        this.messageService.add({
          severity: 'error',
          summary: 'Hata',
          detail: this.errorMessage
        });
      },
      complete: () => {
        console.log('🏁 Register işlemi tamamlandı');
        this.loading = false;
      }
    });
  }

  async signInWithGoogle() {
    if (this.isGoogleLoading) return;

    this.isGoogleLoading = true;
    this.errorMessage = '';

    try {
      await this.authService.signInWithGoogle();
      this.messageService.add({
        severity: 'success',
        summary: 'Başarılı',
        detail: 'Google ile giriş başarılı!'
      });
      this.router.navigate(['/']);
    } catch (error: any) {
      this.errorMessage = error.message || 'Google ile giriş sırasında bir hata oluştu.';
      this.messageService.add({
        severity: 'error',
        summary: 'Hata',
        detail: this.errorMessage || 'Bir hata oluştu'
      });
    } finally {
      this.isGoogleLoading = false;
    }
  }
}
