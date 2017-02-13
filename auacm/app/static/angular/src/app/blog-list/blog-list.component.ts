import { Component, OnInit } from '@angular/core';

import { BlogService } from '../blog.service';

import { BlogPost } from '../models/blog';

@Component({
  templateUrl: './blog-list.component.html',
  styleUrls: ['./blog-list.component.css']
})
export class BlogListComponent implements OnInit {

  blogPosts: BlogPost[] = [];

  searchFilter: string = "";

  constructor(private _blogService: BlogService) { }

  ngOnInit() {
    this.getBlogs();
  };

  getBlogs() {
    this._blogService.getAllBlogPosts().then(blogs => {
      this.blogPosts = blogs;
    });
  };
}
