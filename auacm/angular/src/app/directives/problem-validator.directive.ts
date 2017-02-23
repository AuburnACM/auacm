import { Directive, forwardRef, Attribute } from '@angular/core';
import { Validator, AbstractControl, NG_VALIDATORS } from '@angular/forms';

@Directive({
  selector: '[appProblemValidator][formControlName],[appProblemValidator][formControl],[appProblemValidator][ngModel]',
  providers: [
        { provide: NG_VALIDATORS, useExisting: forwardRef(() => ProblemValidatorDirective), multi: true }
  ]
})
export class ProblemValidatorDirective implements Validator {

  constructor() { }

  validate(c: AbstractControl) {
    if (c.value === null) return null
    var value = c.value.trim();
    var numRegex = new RegExp('^[0-9 -]+$');
    if (numRegex.test(value)) {
      return {
        appProblemValidator: {
          valid: false
        }
      }
    }
    return null;
  }
}
