import { Injectable } from '@angular/core';

import { Observable, Observer, Subject } from 'rxjs/Rx';

@Injectable()
export class WebsocketService {

  private subject: Subject<MessageEvent>;
  private websocket: WebSocket;

  constructor() { }

  connect(url: string) : Subject<MessageEvent> {
    if (!this.subject) {
      this.subject = this.create(window.location.protocol === 'http:' ? 'ws://' + url : 'wss://' + url);
      return this.subject;
    }
    return this.subject;
  }

  private create(url: string) {
    this.websocket = new WebSocket(url);

    var subject = new Subject<MessageEvent>();

    this.websocket.onmessage = event => {
      subject.next(event);
    }

    this.websocket.onerror = event => {
      subject.error(event);
    }

    this.websocket.onclose = event => {
      subject.complete();
    }
    return subject;
  };

  send(data: Object) {
    if (this.websocket !== undefined) {
      if (this.websocket.readyState === WebSocket.OPEN) {
        this.websocket.send(JSON.stringify(data));
      } else {
        console.log('Websocket is not ready.');
      }
    } else {
      console.log('Websocket does not exist!');
    }
  }
}
