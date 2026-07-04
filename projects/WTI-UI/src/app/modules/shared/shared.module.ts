import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

// Angular Material
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';

// App components
import { AppHeaderComponent } from './components/app-header/app-header.component';
import { AppFooterComponent } from './components/app-footer/app-footer.component';
import { LanguageSelectorComponent } from './components/language-selector/language-selector.component';
import { ProblemSelectorComponent } from './components/problem-selector/problem-selector.component';
import { JudgementSelectorComponent } from './components/judgement-selector/judgement-selector.component';
import { AboutWtiComponent } from './components/about-wti/about-wti.component';

// Pipes
import { DisplayTimePipe } from 'src/app/modules/core/services/displayTimePipe.service';

@NgModule({
  providers: [],  //shared modules should not provide services; those should come from CoreModule.
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
	MatTooltipModule,
	DisplayTimePipe
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
	MatTooltipModule
  ]
})
export class SharedModule { }
