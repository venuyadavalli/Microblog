import { Injectable, inject } from '@angular/core';
import { Observable, Observer } from 'rxjs';
import { fetchEventSource } from '@microsoft/fetch-event-source';
import { Auth } from '@angular/fire/auth';

class RetriableError extends Error { }
class FatalError extends Error { }

@Injectable({ providedIn: 'root' })
export class SseService {
  private auth = inject(Auth);

  connect(url: string, options?: { maxRetries?: number, baseRetryMs?: number, maxRetryMs?: number }): Observable<string> {
    return new Observable<string>(subscriber => {
      this.openSseConnection(url, subscriber, options);
      return () => subscriber.complete();
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
    };

    const getRetryDelay = (attempt: number) =>
      Math.min(config.baseRetryMs * Math.pow(2, attempt), config.maxRetryMs);

    const log = (msg: string, err?: any) =>
      console.log(`[SseService] ${msg}`, err ?? '');

    const handleMessage = (event: { data: string }) => {
      retryCount = 0;
      subscriber.next(event.data);
    };

    const handleError = (error: unknown) => {
      if (aborted) return;
      log('Error in SSE connection:', error);
      if (retryCount < config.maxRetries) {
        const delay = getRetryDelay(retryCount++);
        log(`Attempting reconnect #${retryCount} in ${delay} ms...`);
        setTimeout(() => { if (!aborted) openSse(); }, delay);
      } else {
        subscriber.error(error);
        cleanup();
      }
    };

    const handleOpen = async (response: Response) => {
      if (response.ok) return;
      if (response.status >= 400 && response.status < 500 && response.status !== 429)
        throw new FatalError('Client error (not retryable)');
      throw new RetriableError('Temporary server error');
    };

    const openSse = async () => {
      try {
        const user = this.auth.currentUser;
        if (!user) throw new FatalError('No authenticated user');
        const token = await user.getIdToken();
        fetchEventSource(url, {
          headers: { 'Authorization': `Bearer ${token}` },
          signal: controller.signal,
          async onopen(response) { await handleOpen(response); },
          onmessage: handleMessage,
          onerror: (err) => { if (!(err instanceof FatalError)) handleError(err); else throw err; },
          onclose: () => handleError(new RetriableError('Connection closed')),
        });
      } catch (err) {
        handleError(err);
      }
    };

    openSse();
    return cleanup;
  }
}
