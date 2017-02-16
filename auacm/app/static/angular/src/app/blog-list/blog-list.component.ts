import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { BlogService } from '../blog.service';
import { AuthService } from '../auth.service';
import { LimitWordsPipe }from '../pipes/limit-words.pipe';

import { BlogPost } from '../models/blog';
import { UserData } from '../models/user';

@Component({
  templateUrl: './blog-list.component.html',
  styleUrls: ['./blog-list.component.css']
})
export class BlogListComponent implements OnInit {

  blogPosts: BlogPost[] = [];

  user: UserData;

  hoverId: number = -1;

  searchFilter: string = "";

  constructor(private _blogService: BlogService, private _authService: AuthService,
              private _router: Router) {
    this._authService.userData$.subscribe(newData => {
      this.user = newData;
    })
   }

  ngOnInit() {
    this.user = this._authService.getUserData();
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
