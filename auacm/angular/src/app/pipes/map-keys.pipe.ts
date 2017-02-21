import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'mapKeys'
})
export class MapKeysPipe implements PipeTransform {

  transform(value: Map<string, any>): any {
    var array = [];
    for (var property in value) {
      array.push(property);
    }
    return array;
    // return Object.keys(value);
  }

}
