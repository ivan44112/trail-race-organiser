import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly commandApiUrl = environment.commandApiUrl;

  constructor(private http: HttpClient, private router: Router) {}

  login(email: string, role: string) {
    return this.http.post<{ token: string }>(`${this.commandApiUrl}/auth/login`, { email, role });
  }

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }
}
