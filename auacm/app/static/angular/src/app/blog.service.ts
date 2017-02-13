import { Injectable } from '@angular/core';
import { Http, Request, Response } from '@angular/http';
import { Subject } from 'rxjs';

import { BlogPost, BlogAuthor } from './models/blog';

@Injectable()
export class BlogService {

  constructor(private _http: Http) { }

  createBlog(title: string, subtitle: string, body: string) : Promise<BlogPost> {
    return undefined;
  };

  updateBlogPost(postId: number, title: string, subtitle: string, body: string) : Promise<BlogPost> {
    return undefined;
  };

  /**
   * Fetches the blog posts from /api/blog.
   * 
   * @author John Harrison
   */
  getAllBlogPosts() : Promise<BlogPost[]> {
    return new Promise((resolve, reject) => {
      this._http.get('/api/blog').subscribe((res: Response) => {
        var posts = [];
        if (res.status === 200) {
          var data = res.json().data;
          for (var i = 0; i < data.length; i++) {
            posts.push(data[i]);
          }
        }
        resolve(posts);
      }, (err: Response) => {
        resolve([]);
      });
    });
  };

  getBlogPost(blogId: number) : Promise<BlogPost> {
    return undefined;
  };

  deleteBlogPost(blogId: number) : Promise<boolean> {
    return undefined;
  };
}
