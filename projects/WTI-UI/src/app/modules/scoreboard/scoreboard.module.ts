import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ScoreboardPageComponent } from './components/scoreboard-page/scoreboard-page.component';

@NgModule({
  declarations: [
    ScoreboardPageComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    FormsModule
  ],
  exports: [],
  entryComponents: []
})
export class ScoreboardModule {

}
