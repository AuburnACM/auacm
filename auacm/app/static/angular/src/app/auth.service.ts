import { Injectable } from '@angular/core';
import { Http, Request, Response, Headers, URLSearchParams } from '@angular/http';
import { Subject } from 'rxjs';

import { UserData, RankData } from './models/user';

/**
 * All methods of this class should notify the userData observable
 * so that components subscribed can be notified of a change.
 */
@Injectable()
export class AuthService {

  private userDataSource: Subject<UserData> = new Subject<UserData>();
  private userData: UserData = new UserData();

  userData$ = this.userDataSource.asObservable();

  constructor(private _http: Http) { }

  updateUserData(userData: UserData) : void {
    this.userData = userData;
    this.userDataSource.next(userData);
  };

  refreshUserData() : void {
    this.me().then(data => {
      this.updateUserData(data);
    });
  };

  login(username: string, password: string) : Promise<boolean> {
    var self = this;
    return new Promise((resolve, reject) => {
      var headers = new Headers();
      headers.append('Content-Type', 'application/x-www-form-urlencoded');
      var params = new URLSearchParams();
      params.append('username', username);
      params.append('password', password);
      this._http.post('/api/login', params.toString(), {headers: headers}).subscribe((res: Response) => {
        self.refreshUserData();
        if (res.status == 200) {
          resolve(true);
        } else {
          resolve(false);
        }
      }, (err: Response) => {
        self.refreshUserData();
        resolve(false);
      });
    })
  };

  logout() : Promise<boolean> {
    var self = this;
    return new Promise((resolve, reject) => {
      this._http.get('/api/logout').subscribe((res: Response) => {
        self.updateUserData(new UserData());
        if (res.status == 200) {
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

  getUserData() : UserData {
    return this.userData;
  }
  
  me() : Promise<UserData> {
    return new Promise((resolve, reject) => {
      var self = this;
      this._http.get('/api/me').subscribe((res: Response) => {
        if (res.status == 200) {
          var data = res.json().data;
          var userData = new UserData();
          userData.displayName = data.displayName;
          userData.isAdmin = data.isAdmin == 1;
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
  };

  createUser(username: string, password: string, displayName: string) : Promise<boolean> {
    return undefined;
  };

  changePassword(oldPassword: string, newPassword: string) : Promise<boolean> {
    return undefined;
  };

  getRanking(timeframe: string) : Promise<RankData[]> {
    return new Promise((resolve, reject) => {
      var time = timeframe === undefined ? 'all' : timeframe;
      this._http.get(`/api/ranking/${time}`).subscribe((res: Response) => {
        var data = res.json().data;
        var rankings = [];
        if (res.status == 200) {
          for (var i = 0; i < data.length; i++) {
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
	public DAY: string = "day";
	public WEEK: string = "week";
	public MONTH: string = "month";
	public YEAR: string = "year";
	public ALL: string = "all";
}
