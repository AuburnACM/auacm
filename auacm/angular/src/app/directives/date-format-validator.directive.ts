import { Directive, forwardRef, Attribute } from '@angular/core';
import { Validator, AbstractControl, NG_VALIDATORS } from '@angular/forms';

@Directive({
  selector: '[appDateFormatValidator][ngModel]',
  providers: [
        { provide: NG_VALIDATORS, useExisting: forwardRef(() => DateFormatValidatorDirective), multi: true }
  ]
})
export class DateFormatValidatorDirective implements Validator {
  validate(c: AbstractControl): any {
    if (c.value === null || c.value === 'undefined') {
      return null;
    }
    const parts = c.value.split(' ');
    let incorrectFormat = false;
    let datePassed = false;
    const stuff: any = {};

    if (parts.length !== 2) {
      incorrectFormat = true;
      return {
        problemValidatorFormat: {
          valid: false
        }
      };
    }

    const day = parts[0].split('-');
    const time = parts[1].split(':');

    if (day.length !== 3 || time.length !== 2) {
      incorrectFormat = true;
      return {
        problemValidatorFormat: {
          valid: false
        }
      };
    }

    const month = parseInt(day[0], 10) - 1;
    const dayOfMonth = parseInt(day[1], 10);
    const year = parseInt(day[2], 10);
    const hourOfDay = parseInt(time[0], 10);
    const minute = parseInt(time[1], 10);

    incorrectFormat = isNaN(month) || isNaN(dayOfMonth)
      || isNaN(year) || isNaN(hourOfDay) || isNaN(minute);

    datePassed = new Date(year, month, dayOfMonth, hourOfDay, minute).valueOf() < Date.now().valueOf();

    if (incorrectFormat) {
      return {
        problemValidatorFormat: {
          valid: false
        }
      };
    }
    if (datePassed) {
      return {
        problemValidatorPassed: {
          valid: false
        }
      };
    }
    return null;
  }
}
