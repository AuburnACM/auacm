import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'orderBy'
})
export class OrderByPipe implements PipeTransform {

  transform(array: any[], sortByParam: string): any {
    if (array === undefined || array.length <= 0 || array[0][sortByParam] === undefined) {
      return array;
    } else {
      return array.sort(function(a, b) {
        return a[sortByParam] - b[sortByParam];
      });
    }
  }

}
