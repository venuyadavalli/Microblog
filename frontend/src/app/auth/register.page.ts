// auth/register.page.ts
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  standalone: true,
  selector: 'app-register',
  imports: [FormsModule, RouterModule],
  template: `
    <h2>Register</h2>
    <form (ngSubmit)="register()">
      <input type="email" placeholder="Email" [(ngModel)]="email" name="email" required />
      <input type="password" placeholder="Password" [(ngModel)]="password" name="password" required />
      <button type="submit">Register</button>
    </form>
    <a routerLink="/login">Login</a>
  `,
})
export class RegisterPage {
  email = '';
  password = '';

  constructor(private authService: AuthService, private router: Router) {}

  async register() {
    try {
      await this.authService.register(this.email, this.password);
      this.router.navigate(['/login']);
    } catch (error) {
      console.error(error);
    }
  }
}
