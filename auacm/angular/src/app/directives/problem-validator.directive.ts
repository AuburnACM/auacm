import { Directive, forwardRef, Attribute } from '@angular/core';
import { Validator, AbstractControl, NG_VALIDATORS } from '@angular/forms';

@Directive({
  selector: '[appProblemValidator][formControlName],[appProblemValidator][formControl],[appProblemValidator][ngModel]',
  providers: [
        { provide: NG_VALIDATORS, useExisting: forwardRef(() => ProblemValidatorDirective), multi: true }
  ]
})
export class ProblemValidatorDirective implements Validator {
  validate(c: AbstractControl) {
    if (c.value === null) {
      return null;
    }
    const value = c.value.trim();
    const numRegex = new RegExp('^[0-9 -]+$');
    if (numRegex.test(value)) {
      return {
        appProblemValidator: {
          valid: false
        }
      };
    }
    return null;
  }
}
