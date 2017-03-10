import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';

import { UserProfile } from './models/profile';

@Injectable()
export class ProfileService {

  constructor(private _http: Http) { }

  getUserProfile(username: string): Promise<UserProfile> {
    return new Promise((resolve, reject) => {
      this._http.get(`/api/profile/userprofile/${username}`).subscribe((res: Response) => {
        if (res.status === 200) {
          resolve(res.json().data);
        } else {
          resolve(new UserProfile());
        }
      }, (err: Response) => {
        resolve(new UserProfile());
      });
    });
  }
}
