import { Directive, forwardRef, Attribute } from '@angular/core';
import { Validator, AbstractControl, NG_VALIDATORS } from '@angular/forms';

@Directive({
  selector: '[appContestLengthValidator][ngModel]',
  providers: [
        { provide: NG_VALIDATORS, useExisting: forwardRef(() => ContestLengthValidatorDirective), multi: true }
  ]
})
export class ContestLengthValidatorDirective implements Validator {
  validate(c: AbstractControl): any {
    if (c.value === null) {
      return null;
    }
    const parts = c.value.split(':');
    let incorrectFormat = false;
    let timeTooShort = false;

    if (parts.length !== 2) {
      incorrectFormat = true;
      return {
        contestLengthValidatorFormat: {
          valid: false
        }
      };
    }

    incorrectFormat = isNaN(parseInt(parts[0], 10)) ||
      isNaN(parseInt(parts[1], 10));

    const hours = parseInt(parts[0], 10);
    const minutes = parseInt(parts[1], 10);
    timeTooShort = !((hours === 0 && minutes > 0) || (hours > 0 && minutes >= 0));

    if (incorrectFormat) {
      return {
        contestLengthValidatorFormat: {
          valid: false
        }
      };
    }
    if (timeTooShort) {
      return {
        contestLengthValidatorShort: {
          valid: false
        }
      };
    }
    return null;
  }
}
