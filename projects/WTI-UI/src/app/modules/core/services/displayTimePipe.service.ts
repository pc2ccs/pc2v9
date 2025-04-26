import { Pipe, PipeTransform } from '@angular/core';
import { DEBUG_MODE } from 'src/constants'

/** This class defines a "pipe" which accepts a time, represented as a number of seconds, and returns 
 * a formatted string representing the number of days/hours/minutes/seconds represented by the input number of seconds. 
 * "Days" are only included in the output if they are greater than zero.
 * The "pipe" is suitable for use, for example, in an Angular HTML template wanting to display an elapsed number of seconds
 * in terms of hours/mins/secs (see app-header.component.html).
 */
 
@Pipe({
  name: 'displayTime',
  standalone: true
})
export class DisplayTimePipe implements PipeTransform {

   /**
	Returns a formatted string representing the specified elapsed time (in seconds).
    */
  transform(numSecs: number): string {

    const days = Math.floor(numSecs / (60 * 60 * 24));
    const hours = Math.floor((numSecs % (60 * 60 * 24)) / (60 * 60));
    const minutes = Math.floor((numSecs % (60 * 60)) / (60));
    const seconds = Math.floor(numSecs % 60);

    const formattedHours = hours.toString().padStart(2, '0');
    const formattedMinutes = minutes.toString().padStart(2, '0');
    const formattedSeconds = seconds.toString().padStart(2, '0');

  
	if (days > 0) {
		return `${days}d ${formattedHours}:${formattedMinutes}:${formattedSeconds}`;
	} else {
		return `${formattedHours}:${formattedMinutes}:${formattedSeconds}`;
	}

  }
}