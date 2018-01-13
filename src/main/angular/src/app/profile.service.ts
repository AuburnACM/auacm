import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';

import { UserProfile } from './models/profile';
import { SimpleResponse } from './models/response';
import { environment } from '../environments/environment';

@Injectable()
export class ProfileService {

  private profilePictureUpdate: Subject<Boolean> = new Subject();

  constructor(private _http: Http, private _httpClient: HttpClient) { }

  getUserProfile(username: string): Promise<UserProfile> {
    return new Promise((resolve, reject) => {
      if (username.length === 0) {
        resolve(new UserProfile());
      } else {
        this._http.get(`${environment.apiUrl}/profile/${username}`).subscribe((res: Response) => {
          if (res.status === 200) {
            const result: UserProfile = res.json().data;
            result.username = username;
            resolve(result);
          } else {
            resolve(new UserProfile());
          }
        }, (err: Response) => {
          resolve(new UserProfile());
        });
      }
    });
  }

  uploadUserPicture(base64Data: string, username: string): Promise<SimpleResponse> {
    return new Promise((resolve, reject) => {
      this._http.put(`${environment.apiUrl}/profile/${username}/image`, {
        'data': base64Data
      }).subscribe((res: Response) => {
        if (res.status === 200) {
          this.profilePictureUpdate.next(true);
          resolve(new SimpleResponse(true, 'File sent successfully'));
        } else {
          resolve(new SimpleResponse(false, 'Returned non-200 status code'));
        }
      }, (err: Response) => {
        resolve(new SimpleResponse(false, 'Error uploading file'));
      });
    });
  }

  profilePictureObservable(): Observable<Boolean> {
    return this.profilePictureUpdate;
  }

}
