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
    const parsedHtml = document.createElement( 'html' );
    parsedHtml.innerHTML = words;

    const tempChildren = parsedHtml.children[1].childNodes;
    const children = [];
    for (let i = 0; i < tempChildren.length; i++) {
      children.push(tempChildren[i]);
    }

    let finalElement: CustomString = new CustomString('', 0);
    for (let i = 0; i < children.length; i++) {
      finalElement = this.getStringForElement(children[i],
          finalElement.value, finalElement.size, amount);
    }
    return finalElement.value;
  }

  /**
   * Recursively searches through an HTML element and tries to shorten it to the maxSize.
   *
   * @param htmlElement - the element to shorten
   * @param currentString - the current shortened HTML
   * @param currentSize - the word count of currentString
   * @param maxSize - the maximum amount of words allowed in currentString
   * @returns a CustomString containing the shortened element as a string and the size of the element
   */
  getStringForElement(htmlElement: any, currentString: string,
      currentSize: number, maxSize: number): CustomString {
    if (currentSize >= maxSize) {
      return new CustomString(currentString, currentSize);
    }
    if (htmlElement.textContent.trim() === '') {
      return new CustomString(currentString + htmlElement.textContent, currentSize);
    }
    const size = htmlElement.textContent.split(' ').length;
    if (size + currentSize <= maxSize) {
      // If the amount of words in the element plus the current size is
      // less than equal to the maximum length of the element,
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
        return this.shortenElementText(htmlElement, currentString, currentSize, maxSize);
      } else {
        return this.getStringFromChildElements(htmlElement,
            currentString, currentSize, maxSize);
      }
    }
  }

  /**
   * Shortens the text in a single HTML element.
   *
   * @param htmlElement - the element to shorten
   * @param currentString - the current shortened HTML
   * @param currentSize - the word count of currentString
   * @param maxSize - the maximum amount of words allowed in currentString
   * @returns a CustomString containing the shortened element as a string and the size of the element
   */
  shortenElementText(htmlElement: any, currentString: string,
      currentSize: number, maxSize: number): CustomString {
    const customString: CustomString = new CustomString('', currentSize);
    const innerStringArray = htmlElement.textContent.split(' ');
    for (let i = 0; i + currentSize < maxSize; i++) {
      customString.value += innerStringArray[i] + ' ';
      customString.size++;
    }
    customString.value += '...';
    htmlElement.textContent = customString.value;
    if (htmlElement.nodeName === '#text') {
      customString.value = htmlElement.textContent;
    } else {
      customString.value = htmlElement.outerHTML;
    }
    return new CustomString(currentString + customString.value, customString.size);
  }

  /**
   * Loops through an HTML element's children and tries to shorten them.
   *
   * @param htmlElement - the HTML element that's children will be shortened
   * @param currentString - the current shortened HTML
   * @param currentSize - the word count of currentString
   * @param maxSize - the maximum amount of words allowed in currentString
   * @returns a CustomString containing the shortened element as a string and the size of the element
   */
  getStringFromChildElements(htmlElement: any, currentString: string,
      currentSize: number, maxSize: number): CustomString {
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

class CustomString {
  public value: string;
  public size: number;

  constructor(value: string, size: number) {
    this.value = value;
    this.size = size;
  }
}
