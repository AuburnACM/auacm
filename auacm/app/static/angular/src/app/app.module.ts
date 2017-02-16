import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { AppRouteModule } from './app-route/app-route.module';
import { MarkdownModule } from 'angular2-markdown';

import { AppComponent } from './app.component';
import { BlogListComponent } from './blog-list/blog-list.component';
import { BlogPostComponent } from './blog-post/blog-post.component';
import { CompetitionsComponent } from './competitions/competitions.component';
import { ProblemsComponent } from './problems/problems.component';
import { RankingComponent } from './ranking/ranking.component';

import { BlogService } from './blog.service';
import { AuthService } from './auth.service';
import { ProblemService } from './problem.service';
import { CompetitionService } from './competition.service';
import { SubmissionService } from './submission.service';
import { WebsocketService } from './websocket.service';

import { SecondsToDateTimePipe } from './pipes/seconds-to-date-time.pipe';
import { SecondsToHoursPipe } from './pipes/seconds-to-hours.pipe';
import { IifPipe } from './pipes/iif.pipe';
import { LimitWordsPipe } from './pipes/limit-words.pipe';
import { SearchFilterPipe } from './pipes/search-filter.pipe';
import { OrderByPipe } from './pipes/order-by.pipe';
import { JudgeComponent } from './judge/judge.component';
import { CreateUserComponent } from './create-user/create-user.component';
import { CreateBlogComponent } from './create-blog/create-blog.component';
import { EditBlogComponent } from './edit-blog/edit-blog.component';

@NgModule({
  declarations: [
    AppComponent,
    BlogListComponent,
    BlogPostComponent,
    CompetitionsComponent,
    SecondsToDateTimePipe,
    SecondsToHoursPipe,
    IifPipe,
    LimitWordsPipe,
    ProblemsComponent,
    SearchFilterPipe,
    RankingComponent,
    CompetitionsComponent,
    OrderByPipe,
    JudgeComponent,
    CreateUserComponent,
    CreateBlogComponent,
    EditBlogComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    AppRouteModule,
    MarkdownModule.forRoot()
  ],
  providers: [
    BlogService,
    AuthService,
    ProblemService,
    CompetitionService,
    SubmissionService,
    WebsocketService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
