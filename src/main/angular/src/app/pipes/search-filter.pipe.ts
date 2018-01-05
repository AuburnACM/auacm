import { Pipe, PipeTransform } from '@angular/core';

import { Problem } from '../models/problem';

@Pipe({
  name: 'searchFilter'
})
export class SearchFilterPipe implements PipeTransform {
  transform(array: Problem[], search: string): any {
    if (search === '') {
      return array;
    }
    const validMatches = [];
    const searchLowerCase = search.toLowerCase();
    for (let i = 0; i < array.length; i++) {
      if (array[i].name.toLowerCase().startsWith(searchLowerCase)
          || array[i].appeared.toLowerCase().startsWith(searchLowerCase)
          || array[i].difficulty.toString().startsWith(searchLowerCase)) {
        validMatches.push(array[i]);
      }
    }
    return validMatches;
  }
}
