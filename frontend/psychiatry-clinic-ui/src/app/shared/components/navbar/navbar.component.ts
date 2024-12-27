import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ScrollService } from '../../services/scroll.service';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  currentUser: any = null;

  constructor(
    private authService: AuthService,
    private router: Router,
    private scrollService: ScrollService
  ) {
    this.authService.authChange.subscribe(() => {
      this.updateCurrentUser();
    });
  }

  ngOnInit() {
    this.updateCurrentUser();
  }

  private updateCurrentUser() {
    this.currentUser = this.authService.getCurrentUser();
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  logout() {
    this.authService.logout();
    this.currentUser = null;
    this.router.navigate(['/home']);
  }

  onNavClick(event: Event, sectionId: string) {
    event.preventDefault();
    
    if (this.router.url === '/home') {
      this.scrollService.scrollToSection(sectionId);
    } else {
      this.router.navigate(['/home']).then(() => {
        setTimeout(() => {
          this.scrollService.scrollToSection(sectionId);
        }, 100);
      });
    }
  }

  signInWithGoogle(): void {
    google.accounts.id.initialize({
      client_id: '145923964780-fsti66h7o320jdh56udhbscvk7d5n35o.apps.googleusercontent.com',
      callback: (response: any) => {
        this.authService.googleLogin(response.credential).subscribe({
          next: () => {
            this.updateCurrentUser();
            this.router.navigate(['/home']);
          },
          error: (error) => {
            console.error('Google login error:', error);
          }
        });
      }
    });
    google.accounts.id.prompt();
  }
}
