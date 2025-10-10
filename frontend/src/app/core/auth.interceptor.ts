import { inject } from '@angular/core';
import { HttpInterceptorFn } from '@angular/common/http';
import { from } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { Auth } from '@angular/fire/auth';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(Auth);

  function getBearerToken() {
    const user = auth.currentUser;
    return from(user ? user.getIdToken() : Promise.resolve(null));
  }

  function addAuthorizationHeader(token: string | null) {
    return token
      ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
      : req;
  }

  // since auth can change at any time
  // we have to make sure that -during- processing or intercepting request
  // we are very close to the source of truth
  // that is to make sure that we are considering the latest state of auth for token
  // in order to do that whenever auth changes
  // the token might either get null or refreshed
  // in such cases switchMap will be required
  // to make sure to consider latest auth state associated access token
  return getBearerToken().pipe(
    switchMap(token => next(addAuthorizationHeader(token)))
  );
};
