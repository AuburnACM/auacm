import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Converter } from 'showdown';

import { BlogService } from '../blog.service';
import { UserService } from '../user.service';
import { LimitWordsPipe }from '../pipes/limit-words.pipe';

import { BlogPost } from '../models/blog';
import { UserData } from '../models/user';

const EDIT_ICON_NONE: number = -1;

@Component({
  templateUrl: './blog-list.component.html',
  styleUrls: ['./blog-list.component.css']
})

export class BlogListComponent implements OnInit {

  converter: Converter = new Converter();

  blogPosts: BlogPost[] = [];

  user: UserData;
  hoverId: number = EDIT_ICON_NONE;

  constructor(private _blogService: BlogService, private _userService: UserService,
              private _router: Router) {
    this._userService.userData$.subscribe(newData => {
      this.user = newData;
    })
   }

  ngOnInit() {
    this.user = this._userService.getUserData();
    this.getBlogs();
  };

  getBlogs() {
    this._blogService.getAllBlogPosts().then(blogs => {
      var tempPipe = new LimitWordsPipe();
      this.blogPosts = blogs;
      for (var i = 0; i < this.blogPosts.length; i++) {
        if (tempPipe.transform(this.blogPosts[i].body, 200).trim().length < this.blogPosts[i].body.trim().length) {
          this.blogPosts[i].resized = true;
        }
      }
    });
  };

  editPost(id: number) {
    this._router.navigate([`/blog/${id}/edit`]);
  };
}
