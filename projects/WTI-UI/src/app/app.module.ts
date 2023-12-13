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
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule} from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatToolbarModule } from '@angular/material/toolbar';
import { AppTitleService } from 'src/app/modules/core/services/app-title.service';
//import { DeviceDetectorModule } from 'ngx-device-detector';

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
    BrowserAnimationsModule,
    MatButtonModule, 
    MatIconModule,
    MatSidenavModule,
    MatSnackBarModule,
    MatToolbarModule
//	DeviceDetectorModule.forRoot()
  ],
  providers: [AppTitleService],
  bootstrap: [AppComponent]
})
export class AppModule { }
