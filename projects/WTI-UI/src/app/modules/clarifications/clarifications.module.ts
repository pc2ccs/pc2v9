import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ClarificationsPageComponent } from './components/clarifications-page/clarifications-page.component';
import { SharedModule } from '../shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { NewClarificationComponent } from './components/new-clarification/new-clarification.component';
import { NewClarificationAlertComponent } from './components/new-clarification-alert/new-clarification-alert.component';
import { NewClarificationAnnouncementAlertComponent } from './components/new-announcement-clarification-alert/new-announcement-alert.component';

@NgModule({
  declarations: [
    ClarificationsPageComponent,
    NewClarificationComponent,
    NewClarificationAlertComponent,
    NewClarificationAnnouncementAlertComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule
  ],
  exports: []
})
export class ClarificationsModule { }
