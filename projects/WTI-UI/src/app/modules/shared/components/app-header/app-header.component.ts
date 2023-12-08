import { Component } from '@angular/core';
import { AuthService } from 'src/app/modules/core/auth/auth.service';

@Component({
    selector: 'app-header',
    templateUrl: './app-header.component.html',
    styleUrls: ['./app-header.component.scss']
})
export class AppHeaderComponent {

  get showLinks(): boolean { return this._authService.isLoggedIn; }
  get showTeamId(): boolean { return this._authService.isLoggedIn; }

  constructor(private _authService: AuthService) { }
}
