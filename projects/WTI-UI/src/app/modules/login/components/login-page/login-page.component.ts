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
import { DEBUG_MODE } from 'src/constants'
import { ContestClock } from 'src/app/modules/core/models/contest-clock';

@Component({
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss']
})
export class LoginPageComponent implements OnInit, OnDestroy {
  _unsubscribe = new Subject<void>();
  formGroup: FormGroup;
  invalidCreds = false;
  loginStarted = false;
  contestClock: ContestClock = new ContestClock();

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
            this._contestService.isContestRunning = val;
            this._contestService.contestClock.next();
          });

		//get the actual contest clock info from the PC2 server via the Contest Service (which gets it via the WTI Server and its PC2 API)
		this._contestService.getContestClock() 
			.subscribe(
				(data: any) => {
        			if (!data) { 
						console.error ("Unable to get ContestClock from PC2 API via ContestService!");
					} else {						
						//copy the data fields received from the PC2 Server (via the WTI-API) into the local ContestClock object
						this.contestClock.isRunning = data.running ;
						this.contestClock.contestLengthSecs = data.contestLengthInSecs ;
						this.contestClock.elapsedSecs = data.elapsedSecs ;
						this.contestClock.wallClockStartTime = data.wallClockStartTime ;
					}
      			}, 
				(error: any) => {
        			console.error("LoginPageComponent.onSubmit(): getContestClock() subscription callback error: ");
					console.error (error);
      			}
			);
		
       }, (error: any) => {
			console.error ("LoginPageComponent.onSubmit(): AuthService.login() subscription callback error: ");
			console.error(error);
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
