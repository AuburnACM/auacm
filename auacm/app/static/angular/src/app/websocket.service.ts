import { Injectable } from '@angular/core';

import { Observable, Observer, Subject } from 'rxjs/Rx';

@Injectable()
export class WebsocketService {

  private subject: Subject<MessageEvent>;

  constructor() { }

  connect(url: string) : Subject<MessageEvent> {
    if (!this.subject) {
      this.subject = this.create(window.location.protocol === 'http:' ? 'ws://' + url : 'wss://' + url);
      return this.subject;
    }
    return this.subject;
  }

  private create(url: string) {
    var webSocket = new WebSocket(url);

    var thing = new Subject<MessageEvent>();

    webSocket.onmessage = event => {
      thing.next(event);
    }

    webSocket.onerror = event => {
      thing.error(event);
    }

    webSocket.onclose = event => {
      thing.complete();
    }
    
    // var observable = Observable.create((obs: Observer<MessageEvent>) => {
    //   webSocket.onmessage = event => {
    //     obs.next(event);
    //   }
    //   webSocket.onerror = event => {
    //     obs.error(event);
    //   };
    //   webSocket.onclose(obs.complete.bind(obs));
    // });

    var observer = {
      next: (data: Object) => {
        if (webSocket.readyState === WebSocket.OPEN) {
          console.log(data);
          webSocket.send(JSON.stringify(data));
        }
      }
    };
    //return Subject.create(observer, observable);
    return thing;
  }
}
