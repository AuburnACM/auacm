import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { Converter } from 'showdown';

import { BlogService } from '../blog.service';
import { AuthService } from '../auth.service';

import { BlogPost } from '../models/blog';
import { UserData } from '../models/user';

@Component({
  selector: 'app-create-blog',
  templateUrl: './create-blog.component.html',
  styleUrls: ['./create-blog.component.css']
})
export class CreateBlogComponent implements OnInit {

  converter: Converter = new Converter();

  blogPost: BlogPost = new BlogPost();
  userData: UserData;
  formDisabled = false;
  submitFailed = false;
  submitSuccess = false;

  tabSelect: string = "edit";

  constructor(private _authService: AuthService, private _blogService: BlogService,
              private _location: Location, private _router: Router) {
    _authService.userData$.subscribe(data => {
      this.userData = data;
      if (!this.userData.loggedIn || !this.userData.isAdmin) {
        if (this._router.url === '/blogs/create') {
          this._router.navigate(['404']);
        }
      }
      this.blogPost.author.display = this.userData.displayName;
      this.blogPost.author.username = this.userData.username;
      this.blogPost.postTime = new Date().getTime();
      if (!this.userData.isAdmin) { this.formDisabled = true; } else { this.formDisabled = false; }

    })
  }

  ngOnInit() {
    this.userData = this._authService.getUserData();
    this.blogPost.author.display = this.userData.displayName;
    this.blogPost.author.username = this.userData.username;
    this.blogPost.postTime = new Date().getTime();
    if (!this.userData.isAdmin) { 
      this.formDisabled = true;
    } else { 
      this.formDisabled = false;
    }
  }

  postBlog() {
    this.formDisabled = true;
    this._blogService.createBlog(this.blogPost.title, this.blogPost.subtitle, this.blogPost.body).then(post => {
      if (post === undefined) {
        this.formDisabled = false;
        this.submitFailed = true;
      } else {
        this.blogPost = post;
        this.formDisabled = false;
        this.submitSuccess = true;
      }
    });
  };

  back() {
    this._location.back();
  }
}
