import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { DEBUG_MODE } from 'src/constants';

@Injectable({
	providedIn: 'root'   //forces the service to be a singleton across all app components ('root' == "root injector")
})
export class AuthGuard implements CanActivate {
  constructor(private _authService: AuthService, private _router: Router) {
	  if (DEBUG_MODE) {
		  console.log ("Executing AuthGuard constructor...") ;
	  }
  }

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (this._authService.isLoggedIn) {
      return true;
    }

    this._authService.redirectUrl = state.url;
    this._router.navigateByUrl('/login');
    return false;
  }
}
