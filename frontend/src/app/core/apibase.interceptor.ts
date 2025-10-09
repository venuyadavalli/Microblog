import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../environments/environment';

const API_BASE_URL = environment.apiBaseUrl

export const apiBaseInterceptor: HttpInterceptorFn = (req, next) => {
  const updated = req.clone({ url: API_BASE_URL + req.url });
  return next(updated);
};
