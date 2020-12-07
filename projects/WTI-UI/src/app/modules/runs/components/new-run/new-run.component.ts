import { Component, OnInit, OnDestroy, Inject, ViewChildren, QueryList, ElementRef } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { FileSubmission } from 'src/app/modules/core/models/file-submission';
import { Submission } from 'src/app/modules/core/models/submission';
import { takeUntil } from 'rxjs/operators';
import { ITeamsService } from 'src/app/modules/core/abstract-services/i-teams.service';
import { Subject } from 'rxjs';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UiHelperService } from '../../../core/services/ui-helper.service';

export interface DialogData {
  submitType: 'judged' | 'test';
}

@Component({
  selector: 'app-new-run',
  templateUrl: './new-run.component.html',
  styleUrls: ['./new-run.component.scss']
})
export class NewRunComponent implements OnInit, OnDestroy {
  @ViewChildren('fileInput') fileInputs: QueryList<ElementRef>;
  private _unsubscribe = new Subject<void>();
  newSubmissionForm: FormGroup;
  mainFile: FileSubmission;
  additionalFiles: FileSubmission[] = [];
  testFiles: FileSubmission[] = [];
  submitType: 'judged' | 'test';

  constructor(private _teamService: ITeamsService,
              private _formBuilder: FormBuilder,
              private _matDialogRef: MatDialogRef<NewRunComponent>,
              private _uiHelper: UiHelperService,
              @Inject(MAT_DIALOG_DATA) public data: DialogData) { }

  ngOnInit() {
    this.buildForm();
    this.submitType = this.data.submitType;
  }

  ngOnDestroy(): void {
    this._unsubscribe.next();
    this._unsubscribe.complete();
  }

  isReadyToSubmit(): boolean {
    if (this.submitType === 'judged') {
      return this.newSubmissionForm.valid && !!this.mainFile;
    }

    return this.newSubmissionForm.valid && !!this.mainFile && this.testFiles.length > 0;
  }

  dragOver(event) {
    event.preventDefault();
    event.dataTransfer.dropEffect = 'move';
  }

  onDrop(event) {
    event.preventDefault();
  }

  async onFileChange(source: string, event: any) {
    const files: FileList = event.target.files;
    // tslint:disable-next-line:prefer-for-of
    for (let i = 0; i < files.length; i++) {
      switch (source) {
        case 'main': {
          this.mainFile = await this.buildFileSubmission(files[i]);
          break;
        }
        case 'additional': {
          this.additionalFiles.push(await this.buildFileSubmission(files[i]));
          break;
        }
        case 'testdata': {
          this.testFiles.push(await this.buildFileSubmission(files[i]));
          break;
        }
      }
    }
  }

  close(): void {
    this._matDialogRef.close();
  }

  clearNewSubmission(): void {
    this.newSubmissionForm.reset();
  }

  clearFiles(): void {
    this.mainFile = undefined;
    this.additionalFiles = [];
    this.testFiles = [];
    this.fileInputs.forEach(x => x.nativeElement.value = null);
  }

  onSubmitProblem(): void {
    const model = new Submission();
    model.probName = this.newSubmissionForm.get('problem').value;
    model.language = this.newSubmissionForm.get('language').value;
    model.mainFile = this.mainFile;
    model.extraFiles = this.additionalFiles;
    if (this.testFiles && this.testFiles.length > 0) {
      model.testFile = this.testFiles.splice(0, 1)[0];
      model.additionalTestFiles = this.testFiles;
    }
    model.isTest = this.submitType === 'test';
    this._teamService.submitRun(model)
      .pipe(takeUntil(this._unsubscribe))
      .subscribe(_ => {
        this.clearNewSubmission();
        this.close();
        this._uiHelper.alert('Run has been submitted successfully!');
        this._teamService.runsUpdated.next();
      }, (error: any) => {
        this._uiHelper.alert('Error submitting problem! Check console for details');
        console.error(error);
      });
  }

  async buildFileSubmission(file: File) {
    const fileSubmission = new FileSubmission();
    const fileContents = await this.fileReader(file);
    try {
      fileSubmission.byteData = btoa(fileContents);
    } catch (error) {
      this._uiHelper.alert('Binary files are not allowed!');
      fileSubmission.byteData = fileContents;
      // window.location.href = 'http://amishrakefight.org/gfy/';
    }
    fileSubmission.fileName = file.name;
    return fileSubmission;
  }

  fileReader(file: File): Promise<string> {
    const fileReader = new FileReader();

    return new Promise((resolve, reject) => {
      fileReader.onerror = () => {
        fileReader.abort();
        console.error('Couldn\'t read file!');
      };

      fileReader.onload = () => {
        resolve(fileReader.result.toString());
      };
      fileReader.readAsText(file);
    });
  }

  private buildForm(): void {
    this.newSubmissionForm = this._formBuilder.group({
      problem: [undefined, [Validators.required]],
      language: [undefined, [Validators.required]],
      mainFile: [undefined],
      additionalFiles: [],
      testDataFiles: []
    });
  }
}
