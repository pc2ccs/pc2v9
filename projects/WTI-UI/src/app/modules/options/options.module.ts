import { NgModule } from '@angular/core';
import { OptionsPageComponent } from './components/options-page/options-page.component';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ChangePasswordComponent } from './components/change-password/change-password.component';
import { WebsocketDebugComponent } from './components/websocket-debug/websocket-debug.component';

@NgModule({
  declarations: [
    OptionsPageComponent,
    ChangePasswordComponent,
    WebsocketDebugComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    FormsModule
  ],
  exports: [],
  entryComponents: [
    ChangePasswordComponent
  ]
})
export class OptionsModule {

}
