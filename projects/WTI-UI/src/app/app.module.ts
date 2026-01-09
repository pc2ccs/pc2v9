import { NgModule, APP_INITIALIZER } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { CoreModule } from './modules/core/core.module';
import { LoginModule } from './modules/login/login.module';
import { RunsModule } from './modules/runs/runs.module';
import { OptionsModule } from './modules/options/options.module';
import { ClarificationsModule } from './modules/clarifications/clarifications.module';
import { ScoreboardModule } from './modules/scoreboard/scoreboard.module';
import { SharedModule } from './modules/shared/shared.module';

import { AppInitService } from './modules/core/services/app-init.service';
import { AppTitleService } from './modules/core/services/app-title.service';

/**
 * This module defines the outer structure of the WTI-UI Angular application.
 * (The overall Single-Page-App, or SPA, starts in main.ts, which invokes AppModule
 * in this app.module.ts file.) 
 * AppModule in turn bootstraps the AppComponent class by invoking method 
 * AppInitService.initializeApp(), then loading class AppComponent.

*/
export function initializeAppFactory(appInitService: AppInitService) {
  return () => appInitService.initializeApp();
}

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    CoreModule,
    AppRoutingModule,
    SharedModule,
    LoginModule,
    RunsModule,
    OptionsModule,
    ClarificationsModule,
    ScoreboardModule,
    BrowserAnimationsModule
  ],
  providers: [
    AppTitleService,
    {
      provide: APP_INITIALIZER,
      useFactory: (appInit: AppInitService) => () => appInit.initializeApp(),
      deps: [AppInitService],
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
