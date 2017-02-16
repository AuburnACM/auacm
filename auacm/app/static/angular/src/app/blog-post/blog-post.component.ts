import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { AuthService } from '../auth.service';
import { BlogService } from '../blog.service';

import { UserData } from '../models/user';
import { BlogPost } from '../models/blog';

import {Observable} from 'rxjs';
import 'rxjs/add/operator/switchMap';

@Component({
  selector: 'app-blog-post',
  templateUrl: './blog-post.component.html',
  styleUrls: ['./blog-post.component.css']
})
export class BlogPostComponent implements OnInit {

  user: UserData;

  post: BlogPost = new BlogPost();

  constructor(private _authService: AuthService,
              private _blogService: BlogService,
              private _router: Router, private _activeRoute: ActivatedRoute) {
    this._authService.userData$.subscribe(userData => {
      this.user = userData;
    });
  }

  ngOnInit() {
    this.user = this._authService.getUserData();
    this._activeRoute.params.switchMap((params: Params) => params['id'] 
          ? this._blogService.getBlogPost(params['id']) : Observable.of(new BlogPost()))
          .subscribe(blogPost => {
      this.post = blogPost;
    })
  }

}
