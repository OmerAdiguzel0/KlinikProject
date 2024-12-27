import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable, throwError, of } from 'rxjs';
import { tap, catchError, map } from 'rxjs/operators';
import { environment } from '@environments/environment';

interface RegisterResponse {
  token?: string;
  message?: string;
  email?: string;
  userId?: string;
}

interface LoginResponse {
  token: string;
  userId: string;
  role: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {}

  // Kayıt işlemi
  register(registerData: any): Observable<RegisterResponse> {
    const endpoint = `${this.apiUrl}/register`;
    
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json')
      .set('Accept', 'application/json');

    return this.http.post<RegisterResponse>(endpoint, registerData, { 
      headers
    }).pipe(
      tap(response => {
        console.log('✅ Register başarılı!');
        
        // Kayıt olan kullanıcının email'ini kullan
        const userEmail = registerData.email;
        console.log('📧 Mail gönderimi başlıyor:', userEmail);
        
        // Mail gönderimi için yeni endpoint'e istek at
        if (userEmail) {
          // Burada return ekleyerek Observable'ı zincirleyelim
          return this.sendWelcomeEmail(userEmail).pipe(
            tap(
              () => console.log('📧 Hoşgeldiniz maili gönderildi'),
              error => {
                console.error('📧 Mail gönderimi başarısız:', error);
                // Mail hatası olsa bile register işlemini başarılı sayalım
                return of(response);
              }
            )
          );
        }
        return of(response);
      }),
      catchError(error => {
        console.error('❌ Register hatası!');
        console.error('🔍 Hata detayı:', error);
        return throwError(() => error);
      })
    );
  }

  // Hoşgeldiniz maili gönderme metodu
  private sendWelcomeEmail(email: string): Observable<any> {
    const endpoint = `${this.apiUrl}/send-welcome-email`;
    return this.http.post(endpoint, { email }, {
      headers: new HttpHeaders().set('Content-Type', 'application/json')
    });
  }

  // Giriş işlemi
  login(email: string, password: string): Observable<LoginResponse> {
    const endpoint = `${this.apiUrl}/login`;
    
    return this.http.post<LoginResponse>(endpoint, { email, password }).pipe(
      tap(response => {
        if (response.token) {
          localStorage.setItem('token', response.token);
          localStorage.setItem('userId', response.userId);
          localStorage.setItem('role', response.role);
        }
      }),
      catchError(error => {
        // 401 hatası (yetkisiz erişim) durumunda
        if (error.status === 401) {
          return throwError(() => 'Kullanıcı adı veya şifre yanlış');
        }
        // Diğer tüm hata durumları için
        return throwError(() => 'Giriş sırasında bir hata oluştu');
      })
    );
  }

  // Google ile giriş
  async signInWithGoogle(): Promise<any> {
    // Google sign-in implementasyonu gelecek
    throw new Error('Google ile giriş henüz implement edilmedi');
  }

  // Çıkış işlemi
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('role');
  }

  // Token kontrolü
  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  // Kullanıcı rolü kontrolü
  getUserRole(): string | null {
    return localStorage.getItem('role');
  }

  // Token'ı al
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // Kullanıcı ID'sini al
  getUserId(): string | null {
    return localStorage.getItem('userId');
  }
} 