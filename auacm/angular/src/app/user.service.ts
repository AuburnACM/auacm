import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Subject } from 'rxjs/Subject';

import { UserData, RankData } from './models/user';
import { SimpleResponse } from './models/response';
import { DataWrapper } from './models/datawrapper';
import { UrlEncodedHeader } from './models/service.utils';

/**
 * All methods of this class should notify the userData observable
 * so that components subscribed can be notified of a change.
 */
@Injectable()
export class UserService {
  private userDataSource: Subject<UserData> = new Subject<UserData>();
  private userData: UserData = new UserData();
  public userData$ = this.userDataSource.asObservable();

  constructor(private _httpClient: HttpClient) { }

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
      const form = new FormData();
      form.append('username', username);
      form.append('password', password);
      this._httpClient.post('/api/login', form, {withCredentials: true}).subscribe(data => {
        self.refreshUserData();
        resolve(true);
      }, (err: Response) => {
        self.refreshUserData();
        resolve(false);
      });
    });
  }

  logout(): Promise<boolean> {
    const self = this;
    return new Promise((resolve, reject) => {
      this._httpClient.get('/api/logout', {withCredentials: true}).subscribe(data => {
        self.updateUserData(new UserData());
        resolve(true);
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
      this._httpClient.get<DataWrapper<UserData>>('/api/me', {withCredentials: true}).subscribe(data => {
        const userData = new UserData().deserialize(data.data);
        self.updateUserData(userData);
        resolve(userData);
      }, (err: Response) => {
        resolve(new UserData());
      });
    });
  }

  createUser(username: string, password: string, displayName: string): Promise<SimpleResponse> {
    return new Promise((resolve, reject) => {
      const params = new FormData();
      params.append('username', username);
      params.append('password', password);
      params.append('display', displayName);

      this._httpClient.post('/api/create_user', params).subscribe(res => {
          resolve(new SimpleResponse(true, 'User created successfully.'));
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
      const params = new FormData();
      params.append('oldPassword', oldPassword);
      params.append('newPassword', newPassword);

      this._httpClient.post('/api/change_password', params).subscribe((res: Response) => {
        resolve(res.status === 200);
      }, (err: Response) => {
        resolve(false);
      });
    });
  }

  changeDisplayName(newDisplayName: string): Promise<boolean> {
    return new Promise((resolve, reject) => {
      const params = new FormData();
      params.append('newDisplayName', newDisplayName);

      this._httpClient.post('/api/change_display_name', params).subscribe((res: Response) => {
        resolve(res.status === 200);
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
      this._httpClient.get<DataWrapper<RankData[]>>(`/api/ranking/${time}`).subscribe(data => {
        const ranks = data.data;
        const rankings = [];
        for (let i = 0; i < ranks.length; i++) {
          rankings.push(new RankData().deserialize(ranks[i]));
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
