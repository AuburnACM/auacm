import { Injectable } from '@angular/core';
import { Http, Request, Response, URLSearchParams, Headers } from '@angular/http';
import { Subject } from 'rxjs/Subject';

import { BlogPost, BlogAuthor } from './models/blog';
import { UrlEncodedHeader } from './models/service.utils';

@Injectable()
export class BlogService {

  constructor(private _http: Http) { }

  createBlog(title: string, subtitle: string, body: string): Promise<BlogPost> {
    return new Promise((resolve, reject) => {
      const params = new URLSearchParams();
      params.append('title', title);
      params.append('subtitle', subtitle);
      params.append('body', body);

      this._http.post(`/api/blog`, params.toString(), { headers: UrlEncodedHeader })
          .subscribe((res: Response) => {
        if (res.status === 200) {
          resolve(res.json().data);
        } else {
          resolve(undefined);
        }
      }, (err: Response) => {
        resolve(undefined);
      });
    });
  }

  updateBlogPost(postId: number, title: string, subtitle: string, body: string): Promise<BlogPost> {
    return new Promise((resolve, reject) => {
      const params = new URLSearchParams();
      params.append('title', title);
      params.append('subtitle', subtitle);
      params.append('body', body);

      this._http.put(`/api/blog/${postId}`, params.toString(), { headers: UrlEncodedHeader }).subscribe((res: Response) => {
        if (res.status === 200) {
          resolve(res.json().data);
        } else {
          resolve(undefined);
        }
      }, (err: Response) => {
        resolve(undefined);
      });
    });
  }

  /**
   * Fetches the blog posts from /api/blog.
   */
  getAllBlogPosts(): Promise<BlogPost[]> {
    return new Promise((resolve, reject) => {
      this._http.get('/api/blog').subscribe((res: Response) => {
        const posts = [];
        if (res.status === 200) {
          const data = res.json().data;
          for (let i = 0; i < data.length; i++) {
            posts.push(data[i]);
          }
        }
        resolve(posts);
      }, (err: Response) => {
        resolve([]);
      });
    });
  }

  getBlogPost(blogId: number): Promise<BlogPost> {
    return new Promise((resolve, reject) => {
      this._http.get(`/api/blog/${blogId}`).subscribe((res: Response) => {
        if (res.status === 200) {
          resolve(res.json().data);
        } else {
          resolve(new BlogPost());
        }
      }, (err: Response) => {
        resolve(new BlogPost());
      });
    });
  }
}
