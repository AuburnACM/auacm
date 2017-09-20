import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Subject } from 'rxjs/Subject';

import { BlogPost, BlogAuthor } from './models/blog';
import { DataWrapper } from './models/datawrapper';
import { UrlEncodedHeader } from './models/service.utils';

@Injectable()
export class BlogService {

  constructor(private _httpClient: HttpClient) { }

  createBlog(title: string, subtitle: string, body: string): Promise<BlogPost> {
    return new Promise((resolve, reject) => {
      const params = new FormData();
      params.append('title', title);
      params.append('subtitle', subtitle);
      params.append('body', body);
      const headers = new HttpHeaders();
      // headers.append('Content-Type', 'multipart/form-data');

      this._httpClient.post<DataWrapper<BlogPost>>(`/api/blog`, params, {withCredentials: true, responseType: 'json'})
          .subscribe(data => {
        resolve(data.data);
      }, (err: HttpErrorResponse) => {
        resolve(undefined);
      });
    });
  }

  updateBlogPost(postId: number, title: string, subtitle: string, body: string): Promise<BlogPost> {
    return new Promise((resolve, reject) => {
      const params = new HttpParams();
      params.append('title', title);
      params.append('subtitle', subtitle);
      params.append('body', body);

      this._httpClient.put<DataWrapper<BlogPost>>(`/api/blog/${postId}`, params.toString(), { params: params }).subscribe(data => {
        resolve(data.data);
      }, (err: HttpErrorResponse) => {
        resolve(undefined);
      });
    });
  }

  /**
   * Fetches the blog posts from /api/blog.
   */
  getAllBlogPosts(): Promise<BlogPost[]> {
    return new Promise((resolve, reject) => {
      this._httpClient.get<DataWrapper<BlogPost[]>>('/api/blog').subscribe(data => {
        resolve(data.data);
      }, (err: Response) => {
        resolve([]);
      });
    });
  }

  getBlogPost(blogId: number): Promise<BlogPost> {
    return new Promise((resolve, reject) => {
      this._httpClient.get<DataWrapper<BlogPost>>(`/api/blog/${blogId}`).subscribe(data => {
        resolve(data.data);
      }, (err: Response) => {
        resolve(new BlogPost());
      });
    });
  }
}
