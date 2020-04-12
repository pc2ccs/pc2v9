import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent implements OnInit {
  changePWForm: FormGroup;

  constructor(private _formBuilder: FormBuilder,
              private _dialogRef: MatDialogRef<ChangePasswordComponent>) { }

  ngOnInit() {
    this.buildPWForm();
  }

  close(): void {
    this._dialogRef.close();
  }

  private buildPWForm(): void {
    this.changePWForm = this._formBuilder.group({
      oldPassword: [undefined, Validators.required],
      newPassword: [undefined, Validators.required],
      repeatNewPassword: [undefined, Validators.required]
    });
  }

}
