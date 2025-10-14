import { Routes } from '@angular/router';
import { authGuard } from './auth/auth.guard';
import { ForgotPasswordPage } from './auth/forgot-password.page';
import { LoginPage } from './auth/login.page';
import { RegisterPage } from './auth/register.page';
import { WelcomePage } from './welcome.page';
import { SseDemoComponent } from './demo/sse.page';

export const routes: Routes = [
  { path: 'login', component: LoginPage },
  { path: 'register', component: RegisterPage },
  { path: 'forgot-password', component: ForgotPasswordPage },
  { path: 'welcome', component: WelcomePage, canActivate: [authGuard] },
  { path: 'demo', component: SseDemoComponent },
  { path: '', redirectTo: '/demo', pathMatch: 'full' },
];
