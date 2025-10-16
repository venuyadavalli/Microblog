import { Injectable, inject } from '@angular/core';
import { Observable, Observer } from 'rxjs';
import { fetchEventSource, EventSourceMessage } from '@microsoft/fetch-event-source';
import { Auth } from '@angular/fire/auth';

@Injectable({ providedIn: 'root' })
export class SseService {
  private auth = inject(Auth);

  connect(
    url: string,
    options?: { maxRetries?: number, baseRetryMs?: number, maxRetryMs?: number }
  ): Observable<string> {
    return new Observable<string>(subscriber => {
      const cleanup = this.openSseConnection(url, subscriber, options);
      return () => cleanup();
    });
  }

  private openSseConnection(
    url: string,
    subscriber: Observer<string>,
    options?: { maxRetries?: number, baseRetryMs?: number, maxRetryMs?: number }
  ) {
    const config = {
      maxRetries: options?.maxRetries ?? 5,
      baseRetryMs: options?.baseRetryMs ?? 3000,
      maxRetryMs: options?.maxRetryMs ?? 30000,
    };

    let retryCount = 0;
    let aborted = false;
    const controller = new AbortController();

    const cleanup = () => {
      aborted = true;
      controller.abort();
      subscriber.complete();
    };

    const getRetryDelay = (attempt: number) =>
      Math.min(config.baseRetryMs * Math.pow(2, attempt), config.maxRetryMs);

    const handleMessage = (event: EventSourceMessage) => {
      retryCount = 0;
      try {
        subscriber.next(event.data);
      } catch (e) {
        subscriber.next(event.data);
      }
    };

    const handleError = (error: unknown) => {
      if (aborted) return;
      if (retryCount < config.maxRetries) {
        const delay = getRetryDelay(retryCount++);
        setTimeout(() => { if (!aborted) openSse(); }, delay);
      } else {
        subscriber.error(error);
        cleanup();
      }
    };

    const handleOpen = async (response: Response) => {
      if (response.ok) return;
      if (response.status >= 400 && response.status < 500 && response.status !== 429)
        throw new Error('Client error (not retryable)');
      throw new Error('Temporary server error');
    };

    const openSse = async () => {
      try {
        const user = this.auth.currentUser;
        if (!user) throw new Error('No authenticated user');
        const token = await user.getIdToken();
        await fetchEventSource(url, {
          headers: { 'Authorization': `Bearer ${token}` },
          signal: controller.signal,
          async onopen(response) { await handleOpen(response); },
          onmessage: handleMessage,
          onerror: handleError,
          onclose: () => handleError(new Error('Connection closed')),
        });
      } catch (err) {
        handleError(err);
      }
    };

    openSse();
    return cleanup;
  }
}
