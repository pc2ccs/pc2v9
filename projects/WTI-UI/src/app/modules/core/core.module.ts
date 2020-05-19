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

export function TeamsServiceFactory(http: HttpClient) {
  if (environment.useMock) { return new TeamsMockService(); }
  return new TeamsService(http);
}

export function ContestServiceFactory(http: HttpClient) {
  if (environment.useMock) { return new ContestMockService(); }
  return new ContestService(http);
}

export function WebsocketServiceFactory(injector: Injector, authService: AuthService) {
  if (environment.useMock) { return new WebsocketMockService(injector); }
  return new WebsocketService(injector, authService);
}

@NgModule({
  providers: [
    { provide: ITeamsService, useFactory: TeamsServiceFactory, deps: [HttpClient] },
    { provide: IContestService, useFactory: ContestServiceFactory, deps: [HttpClient] },
    { provide: AuthService, useClass: AuthService },
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: IWebsocketService, useFactory: WebsocketServiceFactory, deps: [Injector, AuthService] },
    AuthGuard,
    UiHelperService
  ],
  imports: [
    HttpClientModule,
    SharedModule
  ],
  exports: [],
})
export class CoreModule { }
