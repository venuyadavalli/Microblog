import { Routes } from '@angular/router';
import { LoginPage } from './auth/login.page';
import { RegisterPage } from './auth/register.page';
import { ForgotPasswordPage } from './auth/forgot-password.page';
import { authGuard } from './auth/auth.guard';
import { WelcomePage } from './welcome.page';

export const routes: Routes = [
  { path: 'login', component: LoginPage },
  { path: 'register', component: RegisterPage },
  { path: 'forgot-password', component: ForgotPasswordPage },
  { path: 'welcome', component: WelcomePage, canActivate: [authGuard] },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
];
