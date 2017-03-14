import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';

import { UserProfile } from './models/profile';
import { SimpleResponse } from './models/response';

@Injectable()
export class ProfileService {

  constructor(private _http: Http) { }

  getUserProfile(username: string): Promise<UserProfile> {
    return new Promise((resolve, reject) => {
      if (username.length === 0) {
        resolve(new UserProfile());
      } else {
        this._http.get(`/api/profile/userprofile/${username}`).subscribe((res: Response) => {
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
      this._http.put(`/api/profile/image/${username}`, {
        'data': base64Data,
        'mimetype': 'image/png'
      }).subscribe((res: Response) => {
        if (res.status === 200) {
          resolve(new SimpleResponse(true, 'File sent successfully'));
        } else {
          resolve(new SimpleResponse(false, 'Returned non-200 status code'));
        }
      }, (err: Response) => {
        resolve(new SimpleResponse(false, 'Error uploading file'));
      });
    });
  }
}
