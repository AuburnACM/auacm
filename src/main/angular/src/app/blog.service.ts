import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Subject } from 'rxjs/Subject';

import { BlogPost, BlogAuthor } from './models/blog';
import { DataWrapper } from './models/datawrapper';
import { UrlEncodedHeader } from './models/service.utils';
import { environment } from './../environments/environment';

@Injectable()
export class BlogService {

  constructor(private _httpClient: HttpClient) { }

  createBlog(title: string, subtitle: string, body: string): Promise<BlogPost> {
    return new Promise((resolve, reject) => {
      const params = new FormData();
      params.append('title', title);
      params.append('subtitle', subtitle);
      params.append('body', body);

      this._httpClient.post<DataWrapper<BlogPost>>(`${environment.apiUrl}/blog`, params, {withCredentials: true, responseType: 'json'})
          .subscribe(data => {
        resolve(new BlogPost().deserialize(data.data));
      }, (err: HttpErrorResponse) => {
        resolve(undefined);
      });
    });
  }

  updateBlogPost(postId: number, title: string, subtitle: string, body: string): Promise<BlogPost> {
    return new Promise((resolve, reject) => {
      const params = new FormData();
      params.append('title', title);
      params.append('subtitle', subtitle);
      params.append('body', body);

      this._httpClient.post<DataWrapper<BlogPost>>(`${environment.apiUrl}/blog/${postId}`, params, { withCredentials: true }).subscribe(data => {
        resolve(new BlogPost().deserialize(data.data));
      }, (err: HttpErrorResponse) => {
        resolve(undefined);
      });
    });
  }

  deleteBlogPost(postId: number): Promise<any> {
    return new Promise((resolve, reject) => {
      this._httpClient.delete(`${environment.apiUrl}/blog/${postId}`, { withCredentials: true}).subscribe(() => {
        resolve();
      }, (err: Response) => {
        reject(err);
      });
    });
  }

  /**
   * Fetches the blog posts from /api/blog.
   */
  getAllBlogPosts(): Promise<BlogPost[]> {
    return new Promise((resolve, reject) => {
      this._httpClient.get<DataWrapper<BlogPost[]>>(`${environment.apiUrl}/blog`).subscribe(data => {
        const array: BlogPost[] = [];
        for (const temp of data.data) {
          array.push(new BlogPost().deserialize(temp));
        }
        resolve(array);
      }, (err: Response) => {
        resolve([]);
      });
    });
  }

  getBlogPost(blogId: number): Promise<BlogPost> {
    return new Promise((resolve, reject) => {
      this._httpClient.get<DataWrapper<BlogPost>>(`${environment.apiUrl}/blog/${blogId}`).subscribe(data => {
        resolve(new BlogPost().deserialize(data.data));
      }, (err: Response) => {
        resolve(new BlogPost());
      });
    });
  }
}
