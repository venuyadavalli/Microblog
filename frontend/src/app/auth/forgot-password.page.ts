import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  standalone: true,
  selector: 'app-forgot-password',
  imports: [FormsModule, RouterModule],
  template: `
    <h2>Forgot Password</h2>
    <form (ngSubmit)="submit()">
      <input type="email" placeholder="Email" [(ngModel)]="email" name="email" required />
      <button type="submit">Submit</button>
    </form>
    <a routerLink="/login">Back to Login</a>
  `,
})
export class ForgotPasswordPage {
  email = '';

  constructor(private authService: AuthService, private router: Router) { }

  async submit() {

    try {
      await this.authService.resetPassword(this.email)
      console.log('Password reset email sent with custom link');
    } catch (error) {
      console.error('Password Reset Error:', error);
      throw error;
    }
  }
}
