import { Pipe, PipeTransform } from '@angular/core';

import { Problem } from '../models/problem';

@Pipe({
  name: 'searchFilter'
})
export class SearchFilterPipe implements PipeTransform {

  transform(array: Problem[], search: string): any {
    if (search === "") {return array};
    var validMatches = [];
    for (var i = 0; i < array.length; i++) {
      if (array[i].name.toLowerCase().startsWith(search.toLowerCase()) 
          || array[i].appeared.toLowerCase().startsWith(search.toLowerCase()) 
          || array[i].difficulty.toString().startsWith(search.toLowerCase())) {
        validMatches.push(array[i]);
      }
    }
    return validMatches;
  }
}
