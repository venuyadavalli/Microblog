import { Injectable } from '@angular/core';
import { Auth, createUserWithEmailAndPassword, signInWithEmailAndPassword, sendPasswordResetEmail, confirmPasswordReset } from '@angular/fire/auth';
import { inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private auth = inject(Auth);
  private http = inject(HttpClient)

  async register(email: string, password: string) {
    try {
      const userCredential = await createUserWithEmailAndPassword(this.auth, email, password);
      return userCredential.user;
    } catch (error) {
      console.error('Registration Error:', error);
      throw error;
    }
  }

  async login(email: string, password: string) {
    try {
      const userCredential = await signInWithEmailAndPassword(this.auth, email, password);
      const idToken = await userCredential.user.getIdToken();

      const backendResponse = await firstValueFrom(
        this.http.post(
          '/auth/login',
          { idToken },
          { withCredentials: true }
        )
      );
      console.log('Backend login response:', backendResponse);

      return { firebaseUser: userCredential.user, backendResponse };
    } catch (error) {
      console.error('Login Error:', error);
      throw error;
    }
  }

  async logout() {
    try {
      const backendResponse = await firstValueFrom(
        this.http.post(
          '/auth/logout',
          {},
          { withCredentials: true }
        )
      );
      console.log('Backend logout response:', backendResponse);
      return backendResponse;
    } catch (error) {
      console.error('Logout Error:', error);
      throw error;
    }
  }


  async resetPassword(email: string) {
    try {
      await sendPasswordResetEmail(this.auth, email);
      console.log('Password reset email sent');
    } catch (error) {
      console.error('Password Reset Error:', error);
      throw error;
    }
  }

}
