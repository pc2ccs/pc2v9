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
              private _appTitleService: AppTitleService) { 
			  
	  if (DEBUG_MODE) {
		  console.log ("Executing LoginPageComponent constructor") ;
	  }
	}

  ngOnInit(): void {
	
	if (DEBUG_MODE) {
		console.log ("Executing LoginPageComponent.ngOnInit()") ;
	}

	this._appTitleService.setTitleWithTeamId("Login");

	if (this._authService.token) { 
		if (DEBUG_MODE) {
    			console.log ("  AuthService.token returns positive Truthy value; invoking Router to navigate to ") ;
    			console.log ("    AuthService.defaultRoute '", this._authService.defaultRoute, "'") ;
		}
    		this._router.navigateByUrl(this._authService.defaultRoute); 
	  }

	if (DEBUG_MODE) {
		console.log ("  Invoking buildForm() " ) ;
	}
	this.buildForm();
  }

  ngOnDestroy(): void {
		if (DEBUG_MODE) {
			console.log ("Executing LoginPageComponent.ngOnDestroy(); invoking _unsubscribe.next() and then _unsubscribe.complete()") ;
		}
		this._unsubscribe.next();
		this._unsubscribe.complete();
  }

  onSubmit(): void {
	  if (DEBUG_MODE) {
		  console.log ("Executing LoginPageComponent.onSubmit()...") ;
	  }
    this.loginStarted = true;
    const loginCreds = new LoginCredentials();
    loginCreds.teamName = this.formGroup.get('username').value;
    loginCreds.password = this.formGroup.get('password').value;
    if (DEBUG_MODE) {
    	console.log ("Invoking AuthService.login()") ;
    }
    this._authService.login(loginCreds)
      .pipe(takeUntil(this._unsubscribe))
      .subscribe((result: TeamsLoginResponse) => {
			if (DEBUG_MODE) {
				console.log ("Received callback from subscribing to AuthService.login();" ) ;
				console.log ("  Invoking AuthService.completeLogin()") ;
			}
        this._authService.completeLogin(result.teamId, result.teamName);
			if (DEBUG_MODE) {
				console.log ("  Invoking WebsocketService.startWebsocket()") ;
			}
        this._websocketService.startWebsocket();
        	if (DEBUG_MODE) {
        		console.log ("  invoking ContestService.getisContestRunning() and subscribing to the result") ;
        	}
        this._contestService.getIsContestRunning()
          .subscribe((val: boolean) => {
			if (DEBUG_MODE) {
				console.log (" Subscription callback from ContestService.getIsContestRunning() returned: ", val);
				console.log (" ContestService object has uniqueId", this._contestService.uniqueId);
				//JSON.stringify() can't handle recursive objects like Angular Subjects
				//console.log (JSON.stringify(this._contestService,null,2)); //obj, replacerFunction, indent
				console.log ("   and contents:");
				console.log (this._contestService);
			}
			if (DEBUG_MODE) {
				console.log ("Setting ContestService.isContestRunning to '", val, "'") ;
			}
            this._contestService.isContestRunning = val;
            if (DEBUG_MODE) {
            	console.log ("Invoking ContestService.contestClock.next()") ;
            }
            this._contestService.contestClock.next();
          });
      }, (error: any) => {
    	  if (DEBUG_MODE) {
    		  console.log ("AuthService.login() subscription returned error.") ;
    	  }
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
