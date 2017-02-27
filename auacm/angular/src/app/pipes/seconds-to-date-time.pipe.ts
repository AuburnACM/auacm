import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'secondsToDateTime'
})
export class SecondsToDateTimePipe implements PipeTransform {
  transform(seconds: number): any {
      if (seconds === undefined) {
        return 0;
      }
      return new Date(seconds * 1000);
  }
}
