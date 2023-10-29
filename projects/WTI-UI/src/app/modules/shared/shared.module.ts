import { NgModule } from '@angular/core';
import { LanguageSelectorComponent } from './components/language-selector/language-selector.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProblemSelectorComponent } from './components/problem-selector/problem-selector.component';
import { AppHeaderComponent } from './components/app-header/app-header.component';
import { AppFooterComponent } from './components/app-footer/app-footer.component';
import { RouterModule } from '@angular/router';
import { JudgementSelectorComponent } from './components/judgement-selector/judgement-selector.component';
import { MatDialogModule } from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import { AboutWtiComponent } from './components/about-wti/about-wti.component';

@NgModule({
  declarations: [
    AppHeaderComponent,
    AppFooterComponent,
    LanguageSelectorComponent,
    ProblemSelectorComponent,
    JudgementSelectorComponent,
    AboutWtiComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule,
  ],
  exports: [
    AppHeaderComponent,
    AppFooterComponent,
    LanguageSelectorComponent,
    ProblemSelectorComponent,
    JudgementSelectorComponent,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule,
  ]
})
export class SharedModule { }
