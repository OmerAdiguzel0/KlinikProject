import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { AuthService } from '@shared/services/auth.service';

declare var google: any;

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  email: string = '';
  password: string = '';
  loading: boolean = false;
  isGoogleLoading: boolean = false;
  errorMessage: string = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private messageService: MessageService
  ) {}

  ngOnInit() {
    this.initializeGoogleSignIn();
  }

  private initializeGoogleSignIn() {
    try {
      google?.accounts.id.initialize({
        client_id: '145923964780-d87dfejc413fclitnk12j3b8hvkpr53d.apps.googleusercontent.com',
        callback: this.handleGoogleSignIn.bind(this),
        auto_select: false,
        cancel_on_tap_outside: true,
        ux_mode: 'popup'
      });

      google?.accounts.id.renderButton(
        document.getElementById('google-btn'),
        {
          theme: 'filled_blue',
          size: 'large',
          type: 'standard',
          shape: 'rectangular',
          text: 'continue_with',
          width: 250
        }
      );
    } catch (error: any) {
      console.error('Google Sign-In initialization error:', error);
    }
  }

  signInWithGoogle() {
    this.isGoogleLoading = true;
    try {
      google?.accounts.id.prompt();
    } catch (error: any) {
      console.error('Google Sign-In error:', error);
      this.messageService.add({
        severity: 'error',
        summary: 'Hata',
        detail: 'Google Sign-In başlatılamadı'
      });
      this.isGoogleLoading = false;
    }
  }

  private handleGoogleSignIn(response: any) {
    console.log('Google response:', response);

    if (response.credential) {
      this.authService.googleLogin(response.credential).subscribe({
        next: () => {
          this.messageService.add({
            severity: 'success',
            summary: 'Başarılı',
            detail: 'Google ile giriş başarılı'
          });
          this.router.navigate(['/home']);
        },
        error: (error: any) => {
          console.error('Google login error:', error);
          this.messageService.add({
            severity: 'error',
            summary: 'Hata',
            detail: 'Google ile giriş yapılırken bir hata oluştu'
          });
        },
        complete: () => {
          this.isGoogleLoading = false;
        }
      });
    } else {
      this.isGoogleLoading = false;
      this.messageService.add({
        severity: 'error',
        summary: 'Hata',
        detail: 'Google kimlik bilgileri alınamadı'
      });
    }
  }

  onSubmit() {
    if (this.loading) return;

    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.email, this.password).subscribe({
      next: (response) => {
        this.router.navigate(['/']);
      },
      error: (error: any) => {
        if (typeof error === 'string') {
          this.errorMessage = error;
        } else if (error && error.message) {
          this.errorMessage = error.message;
        } else {
          this.errorMessage = 'Giriş sırasında bir hata oluştu';
        }

        this.loading = false;
        this.password = '';
      },
      complete: () => {
        this.loading = false;
      }
    });
  }
} 