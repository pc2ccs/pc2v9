import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginModule } from './modules/login/login.module';
import { CoreModule } from './modules/core/core.module';
import { RunsModule } from './modules/runs/runs.module';
import { SharedModule } from './modules/shared/shared.module';
import { OptionsModule } from './modules/options/options.module';
import { ClarificationsModule } from './modules/clarifications/clarifications.module';
import { ScoreboardModule } from './modules/scoreboard/scoreboard.module';

@NgModule({
  declarations: [
    AppComponent
  ],
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
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
