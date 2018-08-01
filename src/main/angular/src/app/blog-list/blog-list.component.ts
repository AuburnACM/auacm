import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Converter } from 'showdown';

import { BlogService } from '../blog.service';
import { UserService } from '../user.service';
import { LimitWordsPipe } from '../pipes/limit-words.pipe';

import { BlogPost } from '../models/blog';
import { UserData } from '../models/user';

const EDIT_ICON_NONE: number = -1;
const WORD_LIMIT_SIZE = 200;

declare var $: any;

@Component({
  templateUrl: './blog-list.component.html',
  styleUrls: ['./blog-list.component.css']
})
export class BlogListComponent implements OnInit {
  public converter: Converter = new Converter();
  public blogPosts: BlogPost[] = [];
  public user: UserData;
  public hoverId: number = EDIT_ICON_NONE;
  public wordLimitSize = WORD_LIMIT_SIZE;
  public loadingMore = false;
  public noMoreBlogs = false;
  public limit = 10;
  private page = 0;

  constructor(private _blogService: BlogService, private _userService: UserService,
              private _router: Router) {
    this._userService.userData$.subscribe(newData => {
      this.user = newData;
    });
   }

  ngOnInit() {
    this.user = this._userService.getUserData();
    this.getBlogs();
  }

  getBlogs() {
    this._blogService.getAllBlogPosts(this.limit, this.page).then(blogs => {
      const tempPipe = new LimitWordsPipe();
      this.blogPosts = blogs;
      for (let i = 0; i < this.blogPosts.length; i++) {
        if (tempPipe.transform(this.converter.makeHtml(this.blogPosts[i].body),
            this.wordLimitSize).trim().length < this.converter.makeHtml(this.blogPosts[i].body.trim()).length) {
          this.blogPosts[i].resized = true;
        }
      }
    });
  }

  fetchMoreBlogs() {
    this.loadingMore = true;
    this.page++;
    this._blogService.getAllBlogPosts(this.limit, this.page).then(blogs => {
      if (blogs.length == 0) {
        this.noMoreBlogs = true;
      }
      const tempPipe = new LimitWordsPipe();
      const start = this.blogPosts.length;
      for (const blog of blogs) {
        this.blogPosts.push(blog);
      }
      for (let i = start; i < this.blogPosts.length; i++) {
        if (tempPipe.transform(this.converter.makeHtml(this.blogPosts[i].body),
            this.wordLimitSize).trim().length < this.converter.makeHtml(this.blogPosts[i].body.trim()).length) {
          this.blogPosts[i].resized = true;
        }
      }
      this.loadingMore = false;
    });
  }

  editPost(id: number) {
    this._router.navigate([`/blog/${id}/edit`]);
  }

  deletePost(id: number, index: number) {
    this._blogService.deleteBlogPost(id).then(() => {
      this.blogPosts.splice(index, 1);
    });
  }

  toggleBlogPost(post: BlogPost) {
    post.expanded = !post.expanded;
  }
}
