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

  ping(): void {
    this.http.get('/ping', { responseType: 'text' }).subscribe({
      next: res => console.log('Ping response:', res),
      error: err => console.error('Ping failed:', err)
    });
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
  }

  ngOnInit(): void {
    onAuthStateChanged(this.auth, this.handleAuthChange);
    this.ping();
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
