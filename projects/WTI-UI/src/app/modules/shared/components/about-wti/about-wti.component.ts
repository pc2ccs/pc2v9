import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-about-wti',
  templateUrl: './about-wti.component.html',
  styleUrls: ['./about-wti.component.scss']
})
export class AboutWtiComponent implements OnInit {

  constructor(private _dialogRef: MatDialogRef<AboutWtiComponent>) { }

  ngOnInit() {
  }

  close(): void {
    this._dialogRef.close();
  }

}
