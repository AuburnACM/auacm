import { Pipe, PipeTransform } from '@angular/core';

/**
 * This pipe limits the words in a string to a certain amount.
 */
@Pipe({
  name: 'limitWords'
})
export class LimitWordsPipe implements PipeTransform {
  transform(words: string, amount: number): string {
    let finalString = '';
    const allWords = words.split(' ');
    let count = 0;
    for (let i = 0; i < amount && i < allWords.length; i++) {
      finalString += allWords[i] + ' ';
      count++;
    };
    if (count === amount) {
      return finalString.trim() + ' ...';
    } else {
      return finalString.trim();
    }
  }
}
