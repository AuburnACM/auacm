import { Injectable } from '@angular/core';
import { StompService } from 'ng2-stomp-service/dist/stomp.service';
import { environment } from './../environments/environment';
import { MessageWrapper } from './models/message';

@Injectable()
export class WebsocketService {
  private stompConfig = {
    host: `${environment.apiUrl}/ws`,
    debug: false,
    queue:{'init':false}
  };

  private subscriptions: Map<string, any>;

  constructor(private _stompService: StompService) {
    this.subscriptions = new Map<string, any>();
    this._stompService.configure(this.stompConfig);
    this.connect();
  }

  public connect() {
    this._stompService.startConnect().then(() => {
      console.log('Websocket connected');
    }, reason => {
      console.log(reason);
    }).catch(reason => {
      console.log(reason);
    });
  }

  public disconnect() {
    this._stompService.disconnect().then(data => {
      console.log('Websocket disconnected');
    });
  }

  public stompOn(destination: string, callbacks: Map<string, Function[]>) {
    // If a subscription already exists, let's remove it
    this.stompOff(destination);
    this.subscriptions[destination] = this._stompService.subscribe(destination, function(data) {
      const message = new MessageWrapper().deserialize(data);
      if (callbacks[message.eventType] !== undefined) {
        for (const callback of callbacks[message.eventType]) {
          callback(message.data);
        }
      }
    });
  }

  public doesStompExist(destination: string): boolean {
    return this.subscriptions[destination] !== undefined;
  }

  public stompOff(destination: string) {
    if (this.doesStompExist(destination)) {
      this.subscriptions[destination].unsubscribe();
      this.subscriptions.delete(destination);
    }
  }

  public sendMessage(destination: string, message: any, headers?: Object) {
    this._stompService.send(destination, message, headers);
  }
}
