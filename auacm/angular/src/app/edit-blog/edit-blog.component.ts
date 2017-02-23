import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { Converter } from 'showdown';

import 'rxjs/add/operator/switchMap';
import { Observable } from 'rxjs';

import { UserService } from '../user.service';
import { BlogService } from '../blog.service';

import { UserData } from '../models/user';
import { BlogPost } from '../models/blog';

@Component({
  selector: 'app-edit-blog',
  templateUrl: './edit-blog.component.html',
  styleUrls: ['./edit-blog.component.css']
})
export class EditBlogComponent implements OnInit {

  private converter: Converter = new Converter();

  private userData: UserData;

  private oldBlogPost: BlogPost = new BlogPost();
  private newBlogPost: BlogPost = new BlogPost();

  private tabSelect: string = "edit";

  private formDisabled: boolean = false;
  private success: boolean = false;
  private failed: boolean = false;

  constructor(private _router: Router, private _activeRoute: ActivatedRoute,
              private _userService: UserService, private _blogService: BlogService,
              private _location: Location) {
    _userService.userData$.subscribe(userData => {
      this.userData = userData;
      if (!this.userData.loggedIn || !this.userData.isAdmin) {
        if (this._router.url.startsWith('/blog') && this._router.url.endsWith('/edit')) {
          this._router.navigate(['404']);
        }
      }
    });
  }

  ngOnInit() {
    this.userData = this._userService.getUserData();
    this._activeRoute.params.switchMap((params: Params) => params['id'] ? this._blogService.getBlogPost(params['id']) 
        : Observable.of(new BlogPost())).subscribe(post => {
      this.oldBlogPost.title = post.title;
      this.oldBlogPost.subtitle = post.subtitle;
      this.oldBlogPost.body = post.body;
      this.oldBlogPost.id = post.id;
      this.oldBlogPost.postTime = post.postTime;
      this.oldBlogPost.author.display = post.author.display;
      this.oldBlogPost.author.username = post.author.username;

      this.newBlogPost = post;
    });
  }

  reset() {
    this.newBlogPost.title = this.oldBlogPost.title;
    this.newBlogPost.subtitle = this.oldBlogPost.subtitle;
    this.newBlogPost.body = this.oldBlogPost.body;
    this.newBlogPost.id = this.oldBlogPost.id;
    this.newBlogPost.postTime = this.oldBlogPost.postTime;
    this.newBlogPost.author.display = this.oldBlogPost.author.display;
    this.newBlogPost.author.username = this.oldBlogPost.author.username;
  }

  updatePost() {
    this.formDisabled = true;
    this._blogService.updateBlogPost(this.newBlogPost.id, this.newBlogPost.title, this.newBlogPost.subtitle, this.newBlogPost.body).then(post => {
      if (post != undefined) {
        this.oldBlogPost.title = post.title;
        this.oldBlogPost.subtitle = post.subtitle;
        this.oldBlogPost.body = post.body;
        this.oldBlogPost.id = post.id;
        this.oldBlogPost.postTime = post.postTime;
        this.oldBlogPost.author.display = post.author.display;
        this.oldBlogPost.author.username = post.author.username;

        this.newBlogPost = post;
        this.formDisabled = false;
        this.success = true;
      } else {
        this.failed = true;
        this.formDisabled = false;
      }
    });
  }

  back() {
    this._location.back();
  }
}
