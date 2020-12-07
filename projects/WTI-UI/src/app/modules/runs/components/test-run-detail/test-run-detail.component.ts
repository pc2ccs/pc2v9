import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export class TestRunDetailData {
  problem: string;
  results: string;
}

@Component({
  selector: 'app-test-run-detail',
  templateUrl: './test-run-detail.component.html',
  styleUrls: ['./test-run-detail.component.scss']
})
export class TestRunDetailComponent implements OnInit {
  problem: string;
  results: string;

  constructor(private _dialogRef: MatDialogRef<TestRunDetailComponent>,
              @Inject(MAT_DIALOG_DATA) private _data: TestRunDetailData) { }

  ngOnInit() {
    this.problem = this._data.problem;
    this.results = this._data.results;
  }

  close(): void {
    this._dialogRef.close();
  }
}
