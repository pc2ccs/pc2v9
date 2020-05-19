import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AboutWtiComponent } from '../about-wti/about-wti.component';

@Component({
  selector: 'app-footer',
  templateUrl: './app-footer.component.html',
  styleUrls: ['./app-footer.component.scss']
})
export class AppFooterComponent {
  constructor(private _dialogSvc: MatDialog) { }

  showAbout(): void {
    this._dialogSvc.open(AboutWtiComponent);
  }
}
