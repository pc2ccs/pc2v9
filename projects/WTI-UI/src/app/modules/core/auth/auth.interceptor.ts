import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private _authService: AuthService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const idToken = this._authService.token;

    if (idToken) {
      const cloned = req.clone({
        headers: req.headers.set('team_id', idToken)
      });

      return next.handle(cloned);
    } else {
      return next.handle(req);
    }
  }
}
