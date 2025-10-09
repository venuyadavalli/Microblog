// auth/login.page.ts
import { Component } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth.service';

@Component({
  standalone: true,
  selector: 'app-login',
  imports: [FormsModule, RouterModule],
  template: `
    <h2>Login</h2>
    <form (ngSubmit)="login()">
      <input type="email" placeholder="Email" [(ngModel)]="email" name="email" required />
      <input type="password" placeholder="Password" [(ngModel)]="password" name="password" required />
      <button type="submit">Login</button>
    </form>
    <a routerLink="/register">Register</a> |
    <a routerLink="/forgot-password">Forgot Password?</a>
  `,
})
export class LoginPage {
  email = '';
  password = '';

  constructor(private authService: AuthService, private router: Router) { }

  async login() {
    try {
      const response = await this.authService.login(this.email, this.password);
      console.log('Login successful:', response); // prints UserCredential
      this.router.navigate(['/welcome']); // redirect after login
    } catch (error) {
      console.error(error);
    }
  }
}
