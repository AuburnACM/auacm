import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { UserService } from './user.service';

@Injectable()
export class LoginGuard implements CanActivate {
  constructor(private _userService: UserService, private _router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
      const promise = this._userService.isLoggedIn();
      promise.then(value => {
        if (!value) {
          this._router.navigate(['/404']);
        }
      });
      return promise;
  }
}
