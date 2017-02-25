import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'mapKeys'
})
export class MapKeysPipe implements PipeTransform {

  transform(value: Map<string, any>): any {
    return Object.keys(value);
  }
}
