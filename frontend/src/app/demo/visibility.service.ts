import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TabVisibilityService {
  constructor() {
    this.initVisibilityListener();
  }

  private initVisibilityListener(): void {
    document.addEventListener('visibilitychange', () => {
      const now = new Date();
      const timeString = now.toLocaleTimeString('en-GB', { hour12: false });
      if (document.hidden) {
        console.log(`Tab is now inactive at ${timeString}`);
      } else {
        console.log(`Tab is now active again at ${timeString}`);
      }
    });
  }
}
