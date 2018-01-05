import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'secondsToHours'
})
export class SecondsToHoursPipe implements PipeTransform {
  transform(seconds: number): any {
    const date = new Date(null);
    date.setSeconds(seconds);
    return date.toISOString().substring(11, 19);
  }
}
