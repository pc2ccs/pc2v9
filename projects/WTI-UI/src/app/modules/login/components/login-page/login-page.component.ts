import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoginCredentials } from 'src/app/modules/core/models/login-credentials';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from 'src/app/modules/core/auth/auth.service';
import { TeamsLoginResponse } from 'src/app/modules/core/models/teams-login-response';
import { IWebsocketService } from 'src/app/modules/core/abstract-services/i-websocket.service';
import { Router } from '@angular/router';
import { IContestService } from 'src/app/modules/core/abstract-services/i-contest.service';
import { AppTitleService } from 'src/app/modules/core/services/app-title.service';
import { DEBUG_MODE } from 'src/constants';

@Component({
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss']
})
export class LoginPageComponent implements OnInit, OnDestroy {
  _unsubscribe = new Subject<void>();
  formGroup: FormGroup;
  invalidCreds = false;
  loginStarted = false;

  constructor(private _formBuilder: FormBuilder,
              private _authService: AuthService,
              private _websocketService: IWebsocketService,
              private _router: Router,
              private _contestService: IContestService,
			  private _appTitleService: AppTitleService) { }

  ngOnInit(): void {
	
	this._appTitleService.setTitleWithTeamId("Login");
		
    if (this._authService.token) { this._router.navigateByUrl(this._authService.defaultRoute); }
    this.buildForm();
  }

  ngOnDestroy(): void {
    this._unsubscribe.next();
    this._unsubscribe.complete();
  }

  onSubmit(): void {
    this.loginStarted = true;
    const loginCreds = new LoginCredentials();
    loginCreds.teamName = this.formGroup.get('username').value;
    loginCreds.password = this.formGroup.get('password').value;
    this._authService.login(loginCreds)
      .pipe(takeUntil(this._unsubscribe))
      .subscribe((result: TeamsLoginResponse) => {
        this._authService.completeLogin(result.teamId, result.teamName);
        this._websocketService.startWebsocket();
        this._contestService.getIsContestRunning()
          .subscribe((val: boolean) => {
			if (DEBUG_MODE) {
				console.log("Login-page-component.onSubmit(): ");
				console.log (" Subscription callback from ContestService.getIsContestRunning() returned: ", val);
				console.log (" ContestService object is:")
				console.log (this._contestService);
			}
            this._contestService.isContestRunning = val;
            this._contestService.contestClock.next();
          });
      }, (error: any) => {
        this.invalidCreds = true;
        this.loginStarted = false;
      });
  }

  private buildForm(): void {
    this.formGroup = this._formBuilder.group({
      username: [undefined, [Validators.required]],
      password: [undefined, [Validators.required]]
    });
  }
}
