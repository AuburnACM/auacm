import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'indexToCharCode'
})
export class IndexToCharCodePipe implements PipeTransform {

  transform(value: number): any {
    return String.fromCharCode("A".charCodeAt(0) + value);
  }

}
