import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { Converter } from 'showdown';

import { UserService } from '../user.service';
import { BlogService } from '../blog.service';

import { UserData } from '../models/user';
import { BlogPost } from '../models/blog';

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/switchMap';

@Component({
  selector: 'app-blog-post',
  templateUrl: './blog-post.component.html',
  styleUrls: ['./blog-post.component.css']
})
export class BlogPostComponent implements OnInit {
  public converter: Converter = new Converter();
  public user: UserData;
  public post: BlogPost = new BlogPost();

  constructor(private _userService: UserService,
              private _blogService: BlogService,
              private _router: Router, private _activeRoute: ActivatedRoute) {
    this._userService.userData$.subscribe(userData => {
      this.user = userData;
    });
  }

  ngOnInit() {
    this.user = this._userService.getUserData();
    this._activeRoute.params.switchMap((params: Params) => params['id']
          ? this._blogService.getBlogPost(params['id']) : Observable.of(new BlogPost()))
          .subscribe(blogPost => {
      this.post = blogPost;
    });
  }
}
