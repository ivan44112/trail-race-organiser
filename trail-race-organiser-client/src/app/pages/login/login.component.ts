import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import {FormsModule} from '@angular/forms';
import {NgForOf} from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  imports: [
    FormsModule,
    NgForOf
  ]
})
export class LoginComponent {
  loginRequest = {
    email: '',
    role: 'APPLICANT',
  };

  roles = ['APPLICANT', 'ADMINISTRATOR'];

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    this.authService.login(this.loginRequest.email, this.loginRequest.role).subscribe({
      next: (response) => {
        localStorage.setItem('token', response.token);
        const redirectUrl = this.loginRequest.role === 'APPLICANT'
          ? '/applicant-dashboard'
          : '/administrator-dashboard';
        this.router.navigate([redirectUrl]);
      },
      error: (error) => {
        console.error('Login failed', error);
        alert('Login failed: ' + (error.error.message || 'Unknown error'));
      },
    });
  }
}
