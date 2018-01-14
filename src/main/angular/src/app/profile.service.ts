import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';

import { UserProfile } from './models/profile';
import { SimpleResponse } from './models/response';
import { environment } from '../environments/environment';
import { DataWrapper } from './models/datawrapper';

@Injectable()
export class ProfileService {

  private profilePictureUpdate: Subject<Boolean> = new Subject();

  constructor(private _httpClient: HttpClient) { }

  getUserProfile(username: string): Promise<UserProfile> {
    return new Promise((resolve, reject) => {
      if (username.length === 0) {
        resolve(new UserProfile());
      } else {
        this._httpClient.get<DataWrapper<UserProfile>>(`${environment.apiUrl}/profile/${username}`, {withCredentials: true}).subscribe(data => {
          console.log(data.data);
          resolve(new UserProfile().deserialize(data.data));
        }, (err: Response) => {
          resolve(new UserProfile());
        });
      }
    });
  }

  uploadUserPicture(base64Data: string, username: string): Promise<any> {
    return new Promise((resolve, reject) => {
      this._httpClient.post(`${environment.apiUrl}/profile/${username}/image`,
      {'data': base64Data}, {withCredentials: true}).subscribe(() => {
        resolve();
      }, (err: Response) => {
        reject(err);
      });
    });
  }

  profilePictureObservable(): Observable<Boolean> {
    return this.profilePictureUpdate;
  }

}
