import { Directive, forwardRef, Attribute } from '@angular/core';
import { Validator, AbstractControl, NG_VALIDATORS } from '@angular/forms';

@Directive({
  selector: '[contestLengthValidator][ngModel]',
  providers: [
        { provide: NG_VALIDATORS, useExisting: forwardRef(() => ContestLengthValidatorDirective), multi: true }
  ]
})
export class ContestLengthValidatorDirective implements Validator {

  constructor() { }

  validate(c: AbstractControl): any {
    if (c.value === null) return null
    var parts = c.value.split(':');
    var incorrectFormat = false;
    var timeTooShort = false;

    if (parts.length !== 2) {
      incorrectFormat = true;
      return {
        contestLengthValidatorFormat: {
          valid: false
        }
      }
    }

    incorrectFormat = isNaN(parseInt(parts[0])) ||
      isNaN(parseInt(parts[1]));

    var hours = parseInt(parts[0]);
    var minutes = parseInt(parts[1]);
    timeTooShort = !((hours === 0 && minutes > 0) || (hours > 0 && minutes >= 0));

    if (incorrectFormat) {
      return {
        contestLengthValidatorFormat: {
          valid: false
        }
      }
    }
    if (timeTooShort) {
      return {
        contestLengthValidatorShort: {
          valid: false
        }
      }
    }
    return null;
  }
}
