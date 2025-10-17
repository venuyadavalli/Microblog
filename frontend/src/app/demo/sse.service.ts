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
      return () => {
        console.log('[SSE] Connection cancelled or unsubscribed!');
        cleanup();
      };
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
      if (!aborted) {
        aborted = true;
        console.log('[SSE] Cleanup initiated—connection is being closed.');
        controller.abort();
        subscriber.complete();
      }
    };

    const getRetryDelay = (attempt: number) =>
      Math.min(config.baseRetryMs * Math.pow(2, attempt), config.maxRetryMs);

    const handleMessage = (event: EventSourceMessage) => {
      retryCount = 0;
      try {
        console.log('[SSE] Message received:', event);
        subscriber.next(event.data);
      } catch (e) {
        subscriber.next(event.data);
      }
    };

    const handleError = (error: unknown) => {
      if (aborted) return;
      console.log('[SSE] Connection error:', error);
      if (retryCount < config.maxRetries) {
        const delay = getRetryDelay(retryCount++);
        console.log(`[SSE] Attempting reconnect #${retryCount} in ${delay}ms`);
        setTimeout(() => { if (!aborted) openSse(); }, delay);
      } else {
        console.log('[SSE] Max retries reached—connection will close.');
        subscriber.error(error);
        cleanup();
      }
    };

    const handleOpen = async (response: Response) => {
      if (response.ok) {
        console.log("[SSE] Connection established successfully.");
        return;
      }
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
          onclose: () => {
            console.log('[SSE] Connection closed by server or client.');
            handleError(new Error('Connection closed'));
          },
        });

      } catch (err) {
        handleError(err);
      }
    };

    openSse();
    return cleanup;
  }
}
