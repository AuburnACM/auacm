import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { Converter } from 'showdown';

import { BlogService } from '../blog.service';
import { UserService } from '../user.service';

import { BlogPost } from '../models/blog';
import { UserData } from '../models/user';

@Component({
  selector: 'app-create-blog',
  templateUrl: './create-blog.component.html',
  styleUrls: ['./create-blog.component.css']
})
export class CreateBlogComponent implements OnInit {

  public converter: Converter = new Converter();
  public blogPost: BlogPost = new BlogPost();
  public userData: UserData;
  public formDisabled = false;
  public submitFailed = false;
  public submitSuccess = false;
  public tabSelect = 'edit';

  constructor(private _userService: UserService, private _blogService: BlogService,
              private _location: Location, private _router: Router) {
    _userService.userData$.subscribe(data => {
      this.userData = data;
      if (!this.userData.loggedIn || !this.userData.isAdmin) {
        if (this._router.url === '/blogs/create') {
          this._router.navigate(['404']);
        }
      }
      this.blogPost.author.display = this.userData.displayName;
      this.blogPost.author.username = this.userData.username;
      this.blogPost.postTime = new Date().getTime();
      this.formDisabled = !this.userData.isAdmin;
    });
  }

  ngOnInit() {
    this.userData = this._userService.getUserData();
    this.blogPost.author.display = this.userData.displayName;
    this.blogPost.author.username = this.userData.username;
    this.blogPost.postTime = new Date().getTime();
    if (!this.userData.isAdmin) {
      this.formDisabled = true;
    } else {
      this.formDisabled = false;
    }
  }

  makeBlogPost() {
    this.formDisabled = true;
    this._blogService.createBlog(this.blogPost.title,
        this.blogPost.subtitle, this.blogPost.body).then(post => {
      if (post === undefined) {
        this.formDisabled = false;
        this.submitFailed = true;
      } else {
        this.blogPost = post;
        this.formDisabled = false;
        this.submitSuccess = true;
      }
    });
  }

  back() {
    this._location.back();
  }
}
