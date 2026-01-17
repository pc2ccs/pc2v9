import { NgModule, Injector } from '@angular/core';
import { HttpClientModule, HttpClient, HTTP_INTERCEPTORS } from '@angular/common/http';

// Core services and interfaces
import { ITeamsService } from './abstract-services/i-teams.service';
import { TeamsService } from './services/teams.service';	//needed for factory
import { TeamsMockService } from './services/teams.mock.service';

import { IContestService } from './abstract-services/i-contest.service';
import { ContestService } from './services/contest.service';	//needed for factory
import { ContestMockService } from './services/contest.mock.service';

import { IWebsocketService } from './abstract-services/i-websocket.service';
import { WebsocketService } from './services/websocket.service';	//needed for factory
import { WebsocketMockService } from './services/websocket.mock.service';

import { AuthService } from './auth/auth.service';
import { AuthGuard } from './auth/auth.guard';
import { AuthInterceptor } from './auth/auth.interceptor';
import { UiHelperService } from './services/ui-helper.service';
import { DisplayTimePipe } from './services/displayTimePipe.service';

import { SharedModule } from '../shared/shared.module';

import { DEBUG_MODE } from 'src/constants';
import { environment } from 'src/environments/environment';


export function TeamsServiceFactory(http: HttpClient) {
  if (DEBUG_MODE) {
    console.log("Executing TeamsServiceFactory...")
  }

  if (environment.useMock) { 
    if (DEBUG_MODE) {
      console.log("...about to construct then return new TeamsMockService")
    }
    return new TeamsMockService(); 
  } 

  //not using Mock
  if (DEBUG_MODE) {
    console.log("...about to construct then return new TeamsService")
  }
  return new TeamsService(http);
}

export function ContestServiceFactory(http: HttpClient) {
  if (DEBUG_MODE) {
    console.log("Executing ContestServiceFactory...")
  }

  if (environment.useMock) { 
    if (DEBUG_MODE) {
      console.log("...about to construct then return new ContestMockService")
    }
    return new ContestMockService(); 
  }

  //not using Mock
  if (DEBUG_MODE) {
    console.log("...about to construct then return new ContestService")
  }
  return new ContestService(http);
}

export function WebsocketServiceFactory(injector: Injector, 
										uiHelperService: UiHelperService, iContestService: IContestService,
              							iTeamsService: ITeamsService, authService: AuthService) {
  if (DEBUG_MODE) {
    console.log("Executing WebsocketServiceFactory...")
  }

  if (environment.useMock) { 
    if (DEBUG_MODE) {
      console.log("...about to construct then return new WebsocketMockService")
    }	
    return new WebsocketMockService(injector); 
  }

  //not using Mock
  if (DEBUG_MODE) {
    console.log("...about to construct then return new WebsocketService")
  }

  //original code:
  //return new WebsocketService(injector, authService);
  return new WebsocketService(uiHelperService, iContestService, iTeamsService, authService);
}

@NgModule({
  imports: [
    HttpClientModule,
    SharedModule,
	DisplayTimePipe
  ],
  providers: [
	//interfaces:
    { provide: ITeamsService, useFactory: TeamsServiceFactory, deps: [HttpClient] },
    { provide: IContestService, useFactory: ContestServiceFactory, deps: [HttpClient] },
    { provide: IWebsocketService, useFactory: WebsocketServiceFactory, deps: [Injector, UiHelperService, IContestService, ITeamsService, AuthService] },

	//concrete classes:
    AuthService,
    AuthGuard, 

	//interceptors
	{ provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }

  ],
  declarations: [ ],
  exports: [ ]
})
export class CoreModule { }
