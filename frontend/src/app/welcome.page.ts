import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Auth } from '@angular/fire/auth';
import { onAuthStateChanged, signOut, User } from 'firebase/auth';
import { AuthService } from './auth.service';
import { JsonPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  standalone: true,
  selector: 'app-welcome',
  imports: [JsonPipe],
  template: `
    <h1>Welcome!</h1>
    <p>You are logged in.</p>
    <p>Email: {{ userinfo?.email }}</p>
    <p>Joined: {{ joinedAt }}</p>
    <p><strong>Backend User:</strong> {{ backendUser?.email }}</p>
    <pre>{{ backendUser | json }}</pre>
    <button (click)="logout()">Logout</button>
    <pre>{{ userinfo | json }}</pre>
  `,
})
export class WelcomePage implements OnInit {
  private auth = inject(Auth);
  private router = inject(Router);
  private authService = inject(AuthService);
  private http = inject(HttpClient);

  userinfo: User | null = null;
  joinedAt: string | null = null;
  backendUser: any = null;

  ping(): void {
    this.http.get('/ping', { responseType: 'text' }).subscribe({
      next: res => console.log('Ping response:', res),
      error: err => console.error('Ping failed:', err)
    });
  }

  async getBackendUser(): Promise<void> {
    const user = this.auth.currentUser;
    const token = user ? await user.getIdToken() : null;
    if (token) {
      this.http.get('/auth/me', { headers: { Authorization: `Bearer ${token}` } }).subscribe({
        next: userData => {
          this.backendUser = userData;
          console.log(userData);

        },
        error: err => {
          console.error('Backend user fetch failed:', err);
          this.backendUser = null;
        }
      });
    } else {
      this.backendUser = null;
    }
  }

  formatTimestamp(ts: string | number | undefined | null): string {
    if (!ts) return '';
    const date = new Date(ts);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  }

  handleAuthChange = (user: User | null) => {
    this.userinfo = user;
    this.joinedAt = this.formatTimestamp(user?.metadata?.creationTime ?? null);
    if (user) {
      this.getBackendUser();
    } else {
      this.backendUser = null;
    }
  };

  ngOnInit(): void {
    onAuthStateChanged(this.auth, this.handleAuthChange);
    this.ping();
    this.getBackendUser();
  }

  async logout(): Promise<void> {
    try {
      await this.authService.logout();
      await signOut(this.auth);
      await this.router.navigate(['/login']);
    } catch (error) {
      console.error('Logout error:', error);
    }
  }
}
