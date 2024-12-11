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

/*
LoginPageComponent is the initial page displayed by the WTI-UI Single-Page-Application (SPA).
It gets created as part of the "AppModule" bootstrapping process, which invokes its constructor
causing instantiation of each of the classes listed in the constructor parameter list
(note however that IContestService and IWebsocketService get created via invocation of "factory" methods
which are defined in, and exported from, class CoreModule).

When LoginPageComponent is created, its "ngOnInit()" method gets invoked and checks to see if there is
already a "token" defined in the AuthService class (which would indicate the user is already logged in).
If so, it uses the Router to navigate to the "default route" defined in AuthService, which is the "/runs" page.

Otherwise, it builds a "form" for the user to enter team name and password.  When the user clicks "Submit" on that
form, the LoginPageComponent's "onSubmit()" method is invoked.  onSubmit() invokes the AuthService's "login()" method,
which in turn makes an HTTP request to the login() method of the WTI server.  onSubmit() subscribes to (waits for)
the response to this request, and if login was successful then it completes the login (saving login information in 
the AuthService class and using the Router to transfer to the default "/runs" page), opens a WebSocket to the WTI server,
and invokes the ContestService to determine whether the contest clock is running (using the result to cause an SPA-local
"clock tick", which in turn has the effect of notifying class ProblemSelectorComponent whether or not to display
the list of contest problems).
*/
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
