import { Directive, forwardRef, Attribute } from '@angular/core';
import { Validator, AbstractControl, NG_VALIDATORS } from '@angular/forms';

@Directive({
  selector: '[dateFormatValidator][ngModel]',
  providers: [
        { provide: NG_VALIDATORS, useExisting: forwardRef(() => DateFormatValidatorDirective), multi: true }
  ]
})
export class DateFormatValidatorDirective implements Validator {

  constructor() { }

  validate(c: AbstractControl): any {
    if (c.value === null || c.value === 'undefined') return null
    var parts = c.value.split(' ');
    var incorrectFormat = false;
    var datePassed = false;
    var stuff: any = {};
    
    if (parts.length !== 2) {
      incorrectFormat = true;
      return {
        problemValidatorFormat: {
          valid: false
        }
      }
    }

    var day = parts[0].split('-');
    var time = parts[1].split(':');

    if (day.length !== 3 || time.length !== 2) {
        incorrectFormat = true;
        return {
        problemValidatorFormat: {
          valid: false
        }
      }
    }

    var month = parseInt(day[0]) - 1;
    var dayOfMonth = parseInt(day[1]);
    var year = parseInt(day[2]);
    var hourOfDay = parseInt(time[0]);
    var minute = parseInt(time[1]);

    incorrectFormat = isNaN(month) || isNaN(dayOfMonth) 
      || isNaN(year) || isNaN(hourOfDay) || isNaN(minute);

    datePassed = new Date(year, month, dayOfMonth, hourOfDay, minute).valueOf() < Date.now().valueOf();

    if (incorrectFormat) {
      return {
        problemValidatorFormat: {
          valid: false
        }
      }
    }
    if (datePassed) {
      return {
        problemValidatorPassed: {
          valid: false
        }
      }
    }
    return null;
  }

}
