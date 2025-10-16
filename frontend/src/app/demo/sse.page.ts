import { Component } from '@angular/core';
import { SseService } from './sse.service';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-sse-demo',
  template: `
    <h2>SSE Demo</h2>
    <button (click)="start()">Start SSE</button>
    <button (click)="stop()">Stop</button>
    <button (click)="sendPing()">Send Ping</button>
    <ul>
      <li *ngFor="let msg of messages">{{ msg }}</li>
    </ul>
  `,
  standalone: true,
  providers: [SseService],
  imports: [CommonModule]
})
export class SseDemoComponent {
  messages: string[] = [];
  private sub?: any;

  constructor(private sse: SseService, private http: HttpClient) { }

  start() {
    const url = 'http://localhost:8080/sse/subscribe';
    this.messages = [];
    this.sub = this.sse.connect(url).subscribe({
      next: msg => {
        this.messages.push(msg);
      },
      error: err => {
        this.messages.push('Error: ' + err);
      },
      complete: () => {
        this.messages.push('Stream closed');
      },
    });
  }

  stop() {
    this.sub?.unsubscribe();
  }

  sendPing() {
    const url = '/ping';
    const body = 'from sse page';
    const options = {
      headers: { 'Content-Type': 'text/plain' },
      responseType: 'text' as const
    };

    this.http.post(url, body, options).subscribe({
      next: () => {
        this.messages.push('Ping sent');
      },
      error: error => {
        this.messages.push('Ping error: ' + error.message);
      }
    });
  }
}
