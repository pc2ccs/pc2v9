import { FileSubmission } from './file-submission';

export class Submission {
  probName: string;
  language: string;
  mainFile: FileSubmission = null;
  extraFiles: FileSubmission[] = [];
  testFile: FileSubmission = null;
  additionalTestFiles: FileSubmission[] = [];
  isTest: boolean;
}
