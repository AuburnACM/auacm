import { Pipe, PipeTransform } from '@angular/core';
import { Converter } from 'showdown';

/**
 * This pipe limits the words in a string to a certain amount.
 */
@Pipe({
  name: 'limitWords'
})
export class LimitWordsPipe implements PipeTransform {
  private converter: Converter = new Converter({'tables': true});

  /**
   * This transforms a Markdown string into HTML and shortens it based on the amount.
   * 
   * @param words - the markdown text that needs to be shortened
   * @param amount - the maximum word count of the returned string
   */
  transform(words: string, amount: number): string {
    const htmlWords = this.converter.makeHtml(words);
    var parsedHtml = document.createElement( 'html' );
    parsedHtml.innerHTML = htmlWords;

    const tempChildren = parsedHtml.children[1].childNodes;
    const children = [];
    for (let i = 0; i < tempChildren.length; i++) {
      children.push(tempChildren[i]);
    }

    // let finalHtml = document.createElement( 'html' );
    let finalString = '';
    let size = 0;
    for (let i = 0; i < children.length; i++) {
      const element = this.getStringForElement(children[i], '', size, amount);
      finalString += element.value;
      size = element.size;
    }
    return finalString;
  }

  /**
   * Recursively searches through an HTML element and tries to shorten it to the maxSize.
   * 
   * @param htmlElement - the element to shorten
   * @param currentString - the current string that has been shortened
   * @param currentSize - the size of the currentString
   * @param maxSize - the maximum amount of words that can be within the currentString
   * @returns a CustomString containing the shortened element as a string and the size of the element
   */
  getStringForElement(htmlElement: any, currentString: string, currentSize: number, maxSize: number): CustomString {
    if (currentSize >= maxSize || htmlElement.textContent.trim() === '') {
      return new CustomString(currentString, currentSize);
    }
    const size = htmlElement.textContent.split(' ').length;
    if (size + currentSize <= maxSize) {
      // If the amount of words in the element plus the current size is less than equal to the maximum length of the element,
      // then simply add it to the string.
      if (htmlElement.nodeName === '#text') {
        currentString += htmlElement.textContent;
      } else {
        currentString += htmlElement.outerHTML;
      }
      currentSize += size;
      return new CustomString(currentString, currentSize);
    } else {
      // Else we need to recurse the elements children and attempt to shorten them.
      if (htmlElement.childNodes.length === 0 || (htmlElement.childNodes.length === 1
          && htmlElement.childNodes[0].nodeName === '#text')) {
        // The element is either text or the element has no children other than text
        let tempString = '';
        let tempSize = 0;
        const innerStringArray = htmlElement.textContent.split(' ');
        for (let i = 0; i + currentSize < maxSize; i++) {
          tempString += innerStringArray[i] + ' ';
          tempSize++;
        }
        tempString += '...';
        htmlElement.textContent = tempString.trim();
        if (htmlElement.nodeName === '#text') {
          currentString += htmlElement.textContent;
        } else {
          currentString += htmlElement.outerHTML;
        }
        currentSize += tempSize;
        return new CustomString(currentString, currentSize);
      } else {
        const childNodesTemp = htmlElement.childNodes;
        const childNodes = [];
        for (let i = 0; i < childNodesTemp.length; i++) {
          childNodes.push(childNodesTemp[i]);
        }
        let newString = '';
        for (let i = 0; i < childNodes.length; i++) {
          const customString = this.getStringForElement(childNodes[i], '', currentSize, maxSize);
          newString += customString.value;
          currentSize = customString.size;
        }
        htmlElement.innerHTML = newString;
        currentString += htmlElement.outerHTML;
        return new CustomString(currentString, currentSize);
      }
    }
  }
}

class CustomString {
  public value: string;
  public size: number;

  constructor(value: string, size: number) {
    this.value = value;
    this.size = size;
  }
}
