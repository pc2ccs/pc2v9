import { NgModule } from '@angular/core';
import { LoginPageComponent } from './components/login-page/login-page.component';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { LogoutComponent } from './components/logout/logout.component';

@NgModule({
  declarations: [
    LoginPageComponent,
    LogoutComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  exports: []
})
export class LoginModule { }
