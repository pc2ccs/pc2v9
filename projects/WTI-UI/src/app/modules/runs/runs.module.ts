import { NgModule } from '@angular/core';
import { RunsPageComponent } from './components/runs-page/runs-page.component';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { NewRunComponent } from './components/new-run/new-run.component';
import { NewRunAlertComponent } from './components/new-run-alert/new-run-alert.component';
import { TestRunDetailComponent } from './components/test-run-detail/test-run-detail.component';

@NgModule({
  declarations: [
    RunsPageComponent,
    NewRunComponent,
    NewRunAlertComponent,
    TestRunDetailComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule
  ],
  exports: []
})
export class RunsModule { }
