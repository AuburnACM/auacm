import { Injectable } from '@angular/core';
import { Http, Request, Response, Headers, URLSearchParams } from '@angular/http';
import { Subject } from 'rxjs/Subject';

import { UserData, RankData } from './models/user';
import { SimpleResponse } from './models/response';

/**
 * All methods of this class should notify the userData observable
 * so that components subscribed can be notified of a change.
 */
@Injectable()
export class UserService {

  private userDataSource: Subject<UserData> = new Subject<UserData>();
  private userData: UserData = new UserData();

  userData$ = this.userDataSource.asObservable();

  constructor(private _http: Http) {
  }

  updateUserData(userData: UserData): void {
    this.userData = userData;
    this.userDataSource.next(userData);
  }

  refreshUserData(): void {
    this.me().then(data => {
      this.updateUserData(data);
    });
  }

  login(username: string, password: string): Promise<boolean> {
    const self = this;
    return new Promise((resolve, reject) => {
      const headers = new Headers();
      headers.append('Content-Type', 'application/x-www-form-urlencoded');
      const params = new URLSearchParams();
      params.append('username', username);
      params.append('password', password);
      this._http.post('/api/login', params.toString(), {headers: headers}).subscribe((res: Response) => {
        self.refreshUserData();
        if (res.status === 200) {
          resolve(true);
        } else {
          resolve(false);
        }
      }, (err: Response) => {
        self.refreshUserData();
        resolve(false);
      });
    });
  }

  logout(): Promise<boolean> {
    const self = this;
    return new Promise((resolve, reject) => {
      this._http.get('/api/logout').subscribe((res: Response) => {
        self.updateUserData(new UserData());
        if (res.status === 200) {
          resolve(true);
        } else {
          resolve(false);
        }
      }, (err: Response) => {
        self.updateUserData(new UserData());
        resolve(false);
      });
    });
  }

  getUserData(): UserData {
    return this.userData;
  }

  me(): Promise<UserData> {
    return new Promise((resolve, reject) => {
      const self = this;
      this._http.get('/api/me').subscribe((res: Response) => {
        if (res.status === 200) {
          const data = res.json().data;
          const userData = new UserData();
          userData.displayName = data.displayName;
          userData.isAdmin = data.isAdmin === 1;
          userData.loggedIn = true;
          userData.username = data.username;
          self.updateUserData(userData);
          resolve(userData);
        } else {
          resolve(new UserData());
        }
      }, (err: Response) => {
        resolve(new UserData());
      });
    });
  }

  createUser(username: string, password: string, displayName: string): Promise<SimpleResponse> {
    return new Promise((resolve, reject) => {
      const params = new URLSearchParams();
      params.append('username', username);
      params.append('password', password);
      params.append('display', displayName);

      const headers = new Headers();
      headers.append('Content-Type', 'application/x-www-form-urlencoded');

      this._http.post('/api/create_user', params.toString(), { headers: headers }).subscribe((res: Response) => {
        if (res.status === 200) {
          resolve(new SimpleResponse(true, 'User created successfully.'));
        } else {
          resolve(new SimpleResponse(false, res.json().error));
        }
      }, (err: Response) => {
        if (err.status === 401) {
          resolve(new SimpleResponse(false, 'You need to be an admin to do this.'));
        } else if (err.status === 400) {
          resolve(new SimpleResponse(false, 'That user already exists.'));
        } else {
          resolve(new SimpleResponse(false, 'Failed to create the user.'));
        }
      });
    });
  }

  changePassword(oldPassword: string, newPassword: string): Promise<boolean> {
    return new Promise((resolve, reject) => {
      const params = new URLSearchParams();
      params.append('oldPassword', oldPassword);
      params.append('newPassword', newPassword);

      const headers = new Headers();
      headers.append('Content-Type', 'application/x-www-form-urlencoded');

      this._http.post('/api/change_password', params.toString(), { headers: headers }).subscribe((res: Response) => {
        if (res.status === 200) {
          resolve(true);
        } else {
          resolve(false);
        }
      }, (err: Response) => {
        resolve(false);
      });
    });
  }

  /**
   * We should implement this sometime in the future so that we don't have to edit the
   * database manually if we want to make someone an admin.
   */
  updateAdminStatus(username: string): Promise<SimpleResponse> {
    return undefined;
  }

  getRanking(timeframe: string): Promise<RankData[]> {
    return new Promise((resolve, reject) => {
      const time = timeframe === undefined ? 'all' : timeframe;
      this._http.get(`/api/ranking/${time}`).subscribe((res: Response) => {
        const data = res.json().data;
        const rankings = [];
        if (res.status === 200) {
          for (let i = 0; i < data.length; i++) {
            rankings.push(data[i]);
          }
        }
        resolve(rankings);
      }, (err: Response) => {
        resolve([]);
      });
    });
  }
}

export class TimeFrame {
  public DAY = 'day';
  public WEEK = 'week';
  public MONTH = 'month';
  public YEAR = 'year';
  public ALL = 'all';
}
