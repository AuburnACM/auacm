import { Component, OnInit } from '@angular/core';

import { UserData } from '../models/user';

@Component({
  selector: 'app-blog-post',
  templateUrl: './blog-post.component.html',
  styleUrls: ['./blog-post.component.css']
})
export class BlogPostComponent implements OnInit {

  // Needs to be bound to the AuthService user
  user: UserData = new UserData();

  constructor() { }

  ngOnInit() {
  }

}
