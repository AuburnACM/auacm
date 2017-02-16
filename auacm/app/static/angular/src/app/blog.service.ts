import { Injectable } from '@angular/core';
import { Http, Request, Response, URLSearchParams, Headers } from '@angular/http';
import { Subject } from 'rxjs';

import { BlogPost, BlogAuthor } from './models/blog';

@Injectable()
export class BlogService {

  constructor(private _http: Http) { }

  createBlog(title: string, subtitle: string, body: string) : Promise<BlogPost> {
    return undefined;
  };

  updateBlogPost(postId: number, title: string, subtitle: string, body: string) : Promise<BlogPost> {
    return new Promise((resolve, reject) => {
      var params = new URLSearchParams();
      params.append('title', title);
      params.append('subtitle', subtitle);
      params.append('body', body);

      var headers = new Headers();
      headers.append('Content-Type', 'application/x-www-form-urlencoded');

      this._http.put(`/api/blog/${postId}`, params.toString(), { headers: headers }).subscribe((res: Response) => {
        if (res.status === 200) {
          resolve(res.json().data);
        } else {
          resolve(undefined);
        }
      }, (err: Response) => {
        resolve(undefined);
      })
    });
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
    return new Promise((resolve, reject) => {
      this._http.get(`/api/blog/${blogId}`).subscribe((res: Response) => {
        if (res.status === 200) {
          resolve(res.json().data);
        } else {
          resolve(new BlogPost());
        }
      }, (err: Response) => {
        console.log('Failed to fetch a blog post with the id ' + blogId);
        resolve(new BlogPost());
      })
    });
  };

  deleteBlogPost(blogId: number) : Promise<boolean> {
    return undefined;
  };
}
