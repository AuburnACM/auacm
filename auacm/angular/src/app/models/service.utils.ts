import { Headers } from '@angular/http';

const urlHeader = new Headers();
urlHeader.append('Content-Type', 'application/x-www-form-urlencoded');

export { urlHeader as UrlEncodedHeader };
