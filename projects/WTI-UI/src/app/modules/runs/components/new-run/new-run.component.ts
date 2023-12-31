import { Component, OnInit, OnDestroy, Inject, ViewChildren, QueryList, ElementRef } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { FileSubmission } from 'src/app/modules/core/models/file-submission';
import { Submission } from 'src/app/modules/core/models/submission';
import { takeUntil } from 'rxjs/operators';
import { ITeamsService } from 'src/app/modules/core/abstract-services/i-teams.service';
import { Subject } from 'rxjs';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UiHelperService } from '../../../core/services/ui-helper.service';

//See the comments in method getOSName() regarding these services
//import * as os  from 'os';
//import { DeviceDetectorService } from 'ngx-device-detector';
//import * as child_process from 'child_process' ;


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
    
    model.osName = this.getOSName();

    console.log('getOSName() returned : ' + model.osName);

	//make sure no file names contain blanks (the PC2 server chokes on such filenames)
	if (this.filenameContainsBlanks(this.mainFile, this.additionalFiles, this.testFiles)){
		//pop up an error dialog
	    this._uiHelper.alertError('File names may not contain spaces');
	    console.error('One or more submitted file contains a space in its filename');
  } else if (this.filenameContainsDuplicates(this.mainFile, this.additionalFiles)) {
      this._uiHelper.alert('You may not submit multiple files with the same name');
	    console.error('One or more submitted file have the same filename');
	} else {
		//submit the run
	    this._teamService.submitRun(model)
	      .pipe(takeUntil(this._unsubscribe))
	      .subscribe(_ => {
	        this.clearNewSubmission();
	        this.close();
	        this._uiHelper.alertOk('Run has been submitted successfully!');
	        this._teamService.runsUpdated.next();
	      }, (error: any) => {
	        this._uiHelper.alertError('Error submitting problem! Check console for details');
	        console.error(error);
	      });
	}
  }

  async buildFileSubmission(file: File) {
    const fileSubmission = new FileSubmission();
    const fileContents = await this.fileReader(file);
    try {
      fileSubmission.byteData = btoa(fileContents);
    } catch (error) {
      this._uiHelper.alertError('Binary files are not allowed!');
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

  //returns true if any of the filenames of any of the specified FileSubmissions contain a blank (space); false if none do.
  private filenameContainsBlanks(mainfile: FileSubmission, additionalFiles: FileSubmission [], testFiles: FileSubmission []): boolean {
	if (mainfile.fileName.indexOf(" ") > -1) {
		return true;
	}
	for (var file of additionalFiles) {
		if (file.fileName.indexOf(" ") > -1){
			return true;
		}
	}
	for (var file of testFiles) {
		if (file.fileName.indexOf(" ") > -1) {
			return true;
		}
	}
	//none of the specified FileSubmission files contains a space in its name
	return false;
	}

  //returns true if there are duplicate filenames among any of the specified FileSubmissions; false if all unique.
  private filenameContainsDuplicates(mainfile: FileSubmission, additionalFiles: FileSubmission []): boolean {
    let mainfilename = mainfile.fileName;
    let l = mainfilename.lastIndexOf("/");
    if (l == -1) {
      l = mainfilename.lastIndexOf("\\");
    }
    if (l > -1) {
      mainfilename = mainfilename.substring(l + 1);
    }
    for (let i = 0; i < additionalFiles.length; i++) {
      let otherfilename = additionalFiles[i].fileName;
      l = otherfilename.lastIndexOf("/");
      if (l == -1) {
        l = otherfilename.lastIndexOf("\\");
      }
      if (l > -1) {
        otherfilename = otherfilename.substring(l + 1);
      }
      if (mainfilename === otherfilename) {
        return true;
      }
      for (let j = i + 1; j < additionalFiles.length; j++) {
        let otherfilename2 = additionalFiles[j].fileName;
        l = otherfilename2.lastIndexOf("/");
        if (l == -1) {
          l = otherfilename2.lastIndexOf("\\");
        }
        if (l > -1) {
          otherfilename2 = otherfilename2.substring(l + 1);
        }
        if (otherfilename2 === otherfilename) {
          return true;
        }
      }
    }
    //none of the specified FileSubmission files contains duplicate names
    return false;
  }
	
	//returns a string intended to identify the platform on which the browser is running
	private getOSName() : string {
		
		console.log(navigator.userAgent);
		return navigator.userAgent.toString();
		
// The following were all attempts to get the Angular Typescript/Javascript code to invoke the underlying OS to obtain
// platform information (instead of using the "userAgent", which can vary).  
// All of these attempts failed, most commonly with the browser throwing a "xxx is not a function" error.
// It's not even clear that it's *possible* to do this -- isn't Typescript/Javascript constrained to run inside the browser sandbox?

// Try using the Node "os" module:
//		os = new os();
//		console.log("OS type = " + os.type()) ;
//		console.log("OS release = " + os.release()) ;
//		console.log("OS platform =" + os.platform());
//		console.log("OS version = " + os.version());
//		return os.version();
	
// Try using the  DeviceDetectorService from 'ngx-device-detector' (https://www.npmjs.com/package/ngx-device-detector)
//		this.deviceInfo = this._deviceService.getDeviceInfo();
//		console.log("OS name = " + this._deviceService.os);
//		console.log("OS version = " + this._deviceService.os_version);
//  	console.log("Browser = " + this._deviceService.browser);
//		return this._deviceService.os_version;
//
// Try using the Node 'child_process' package to spawn a host-system process
//		var spawn = require('child_process').spawn;
//		var prc = spawn('java',  ['-jar', '-Xmx512M', '-Dfile.encoding=utf8', 'script/importlistings.jar']);
//		//noinspection JSUnresolvedFunction
//		prc.stdout.setEncoding('utf8');
//		prc.stdout.on('data', function (data) {
//  	var str = data.toString()
//		var lines = str.split(/(\r?\n)/g);
//		console.log(lines.join(""));
//
//		prc.on('close', function (code) {
//    		console.log('process exit code ' + code);
//		});
//
//		return this.deviceInfo;

	}
}
