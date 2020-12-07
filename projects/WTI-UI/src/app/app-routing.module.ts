import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginPageComponent } from './modules/login/components/login-page/login-page.component';
import { RunsPageComponent } from './modules/runs/components/runs-page/runs-page.component';
import { OptionsPageComponent } from './modules/options/components/options-page/options-page.component';
import { AuthGuard } from './modules/core/auth/auth.guard';
import { LogoutComponent } from './modules/login/components/logout/logout.component';
import { ClarificationsPageComponent } from './modules/clarifications/components/clarifications-page/clarifications-page.component';
import { ScoreboardPageComponent } from './modules/scoreboard/components/scoreboard-page/scoreboard-page.component';

const routes: Routes = [
  {
    path: 'login',
    component: LoginPageComponent
  }, {
    path: 'runs',
    component: RunsPageComponent,
    canActivate: [AuthGuard]
  }, {
    path: 'options',
    component: OptionsPageComponent,
    canActivate: [AuthGuard]
  }, {
    path: 'logout',
    component: LogoutComponent,
    canActivate: [AuthGuard]
  }, {
    path: 'clarifications',
    component: ClarificationsPageComponent,
    canActivate: [AuthGuard]
  }, {
    path: 'scoreboard',
    component: ScoreboardPageComponent,
    canActivate: [AuthGuard]
  }, {
    path: '**',
    pathMatch: 'full',
    redirectTo: '/runs'
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
