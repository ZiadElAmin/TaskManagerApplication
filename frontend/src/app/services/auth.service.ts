import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

interface AuthResponse {
  token: string;
  username: string;
}

interface RegisterPayload {
  username: string;
  email: string;
  password: string;
}

interface LoginPayload {
  username: string;
  password: string;
}

const API_BASE = 'http://localhost:8080/api/auth';
const TOKEN_KEY = 'taskmanager_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private tokenSignal = signal<string | null>(sessionStorage.getItem(TOKEN_KEY));

  constructor(private http: HttpClient) {}

  register(payload: RegisterPayload): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API_BASE}/register`, payload).pipe(
      tap((res) => this.setToken(res.token))
    );
  }

  login(payload: LoginPayload): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API_BASE}/login`, payload).pipe(
      tap((res) => this.setToken(res.token))
    );
  }

  logout(): void {
    sessionStorage.removeItem(TOKEN_KEY);
    this.tokenSignal.set(null);
  }

  getToken(): string | null {
    return this.tokenSignal();
  }

  isLoggedIn(): boolean {
    return this.tokenSignal() !== null;
  }

  private setToken(token: string): void {
    sessionStorage.setItem(TOKEN_KEY, token);
    this.tokenSignal.set(token);
  }
}
