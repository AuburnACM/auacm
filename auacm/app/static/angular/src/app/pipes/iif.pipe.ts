import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'iif'
})
export class IifPipe implements PipeTransform {

  transform(input: boolean, trueValue: any, falseValue: any): any {
    return input ? trueValue : falseValue;
  }

}
