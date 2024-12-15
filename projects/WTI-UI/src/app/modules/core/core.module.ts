import { NgModule, Injector } from '@angular/core';
import { HttpClientModule, HttpClient, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ITeamsService } from './abstract-services/i-teams.service';
import { TeamsService } from './services/teams.service';
import { AuthService } from './auth/auth.service';
import { TeamsMockService } from './services/teams.mock.service';
import { environment } from 'src/environments/environment';
import { IContestService } from './abstract-services/i-contest.service';
import { ContestMockService } from './services/contest.mock.service';
import { AuthGuard } from './auth/auth.guard';
import { ContestService } from './services/contest.service';
import { WebsocketService } from './services/websocket.service';
import { AuthInterceptor } from './auth/auth.interceptor';
import { WebsocketMockService } from './services/websocket.mock.service';
import { IWebsocketService } from './abstract-services/i-websocket.service';
import { UiHelperService } from './services/ui-helper.service';
import { SharedModule } from '../shared/shared.module';
import { DEBUG_MODE } from 'src/constants';

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
  providers: [
    { provide: ITeamsService, useFactory: TeamsServiceFactory, deps: [HttpClient] },
    { provide: IContestService, useFactory: ContestServiceFactory, deps: [HttpClient] },
    { provide: AuthService, useClass: AuthService },
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: IWebsocketService, useFactory: WebsocketServiceFactory, deps: [Injector, UiHelperService, IContestService, ITeamsService, AuthService] },
    AuthGuard,
	//TODO:  should the following two still be declared here since they are now listed in the above "deps" list?
    UiHelperService,
    ContestService
  ],
  imports: [
    HttpClientModule,
    SharedModule
  ],
  exports: [],
})
export class CoreModule { }
