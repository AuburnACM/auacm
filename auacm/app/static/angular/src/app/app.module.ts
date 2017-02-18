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
import { CreateBlogComponent } from './create-blog/create-blog.component';
import { CreateUserComponent } from './create-user/create-user.component';
import { EditBlogComponent } from './edit-blog/edit-blog.component';
import { EditProblemComponent } from './edit-problem/edit-problem.component';
import { JudgeComponent } from './judge/judge.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { ProblemsComponent } from './problems/problems.component';
import { RankingComponent } from './ranking/ranking.component';
import { ViewProblemComponent } from './view-problem/view-problem.component';

import { AuthService } from './auth.service';
import { BlogService } from './blog.service';
import { CompetitionService } from './competition.service';
import { ProblemService } from './problem.service';
import { SubmissionService } from './submission.service';
import { WebsocketService } from './websocket.service';

import { IifPipe } from './pipes/iif.pipe';
import { LimitWordsPipe } from './pipes/limit-words.pipe';
import { SearchFilterPipe } from './pipes/search-filter.pipe';
import { OrderByPipe } from './pipes/order-by.pipe';
import { SecondsToDateTimePipe } from './pipes/seconds-to-date-time.pipe';
import { SecondsToHoursPipe } from './pipes/seconds-to-hours.pipe';

import { ProblemValidatorDirective } from './problem-validator.directive';

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
    EditBlogComponent,
    ViewProblemComponent,
    EditProblemComponent,
    NotFoundComponent,
    ProblemValidatorDirective
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
