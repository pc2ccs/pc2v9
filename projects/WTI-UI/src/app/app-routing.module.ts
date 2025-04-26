import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginPageComponent } from './modules/login/components/login-page/login-page.component';
import { RunsPageComponent } from './modules/runs/components/runs-page/runs-page.component';
import { OptionsPageComponent } from './modules/options/components/options-page/options-page.component';
import { AuthGuard } from './modules/core/auth/auth.guard';
import { LogoutComponent } from './modules/login/components/logout/logout.component';
import { ClarificationsPageComponent } from './modules/clarifications/components/clarifications-page/clarifications-page.component';
import { ScoreboardPageComponent } from './modules/scoreboard/components/scoreboard-page/scoreboard-page.component';

/**
 * This class defines the "routes" which the Angular application (specifically, the Angular Router) knows how to follow.
 * Note that ORDER MATTERS; the Router searches the 'Routes' array from the beginning to find a route which matches the
 * path it has been told to attempt to follow.  For this reason, the "login" page will always be the default route.
 * Note that remaining routes are protected by a "canActivate: [AuthGuard]" clause; the effect of this is that if the Router
 * decides to attempt to follow that route, it first invokes the "AuthGuard" class, which determines whether following that
 * route is 'allowed'.  (The criterion defined in "AuthGuard" is that the user is logged in.)
 * If the route specified by the user does not match any other route defined here, the Router defaults to the LAST route, 
 * the one with a "path" of "**".  The effect of this is that any attempt by the user to enter any undefined route in the 
 * browser will cause a transfer to the "/runs" page.
 */
const routes: Routes = [
  {
    path: 'login',
    title: 'Login to PC2',
    component: LoginPageComponent
  }, {
    path: 'runs',
    title: 'Submit Runs',
    component: RunsPageComponent,
    canActivate: [AuthGuard]
  }, {
    path: 'options',
    title: 'Configure Options',
    component: OptionsPageComponent,
    canActivate: [AuthGuard]
  }, {
    path: 'logout',
    title: 'Logout of PC2',
    component: LogoutComponent,
    canActivate: [AuthGuard]
  }, {
    path: 'clarifications',
    title: 'Submit Clarification Request',
    component: ClarificationsPageComponent,
    canActivate: [AuthGuard]
  }, {
    path: 'scoreboard',
    title: 'View Scoreboard',
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
