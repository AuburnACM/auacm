import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { AppRouteModule } from './app-route/app-route.module';
import { DragulaModule } from 'ng2-dragula';

import { AppComponent } from './app.component';
import { BlogListComponent } from './blog-list/blog-list.component';
import { BlogPostComponent } from './blog-post/blog-post.component';
import { CompetitionsComponent } from './competitions/competitions.component';
import { CreateBlogComponent } from './create-blog/create-blog.component';
import { CreateUserComponent } from './create-user/create-user.component';
import { EditBlogComponent } from './edit-blog/edit-blog.component';
import { EditCompetitionComponent } from './edit-competition/edit-competition.component';
import { EditProblemComponent } from './edit-problem/edit-problem.component';
import { JudgeComponent } from './judge/judge.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { ProblemsComponent } from './problems/problems.component';
import { RankingComponent } from './ranking/ranking.component';
import { ViewProblemComponent } from './view-problem/view-problem.component';
import { ViewScoreboardComponent } from './view-scoreboard/view-scoreboard.component';

import { AuthService } from './auth.service';
import { BlogService } from './blog.service';
import { CompetitionService } from './competition.service';
import { ProblemService } from './problem.service';
import { SubmissionService } from './submission.service';
import { WebsocketService } from './websocket.service';

import { IifPipe } from './pipes/iif.pipe';
import { IndexToCharCodePipe } from './pipes/index-to-char-code.pipe';
import { LimitWordsPipe } from './pipes/limit-words.pipe';
import { MapKeysPipe } from './pipes/map-keys.pipe';
import { OrderByPipe } from './pipes/order-by.pipe';
import { SearchFilterPipe } from './pipes/search-filter.pipe';
import { SecondsToDateTimePipe } from './pipes/seconds-to-date-time.pipe';
import { SecondsToHoursPipe } from './pipes/seconds-to-hours.pipe';

import { ProblemValidatorDirective } from './directives/problem-validator.directive';
import { DateFormatValidatorDirective } from './directives/date-format-validator.directive';
import { ContestLengthValidatorDirective } from './directives/contest-length-validator.directive';
import { EditTeamsComponent } from './edit-teams/edit-teams.component';
import { EditUserComponent } from './edit-user/edit-user.component';


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
    ProblemValidatorDirective,
    ViewScoreboardComponent,
    MapKeysPipe,
    EditCompetitionComponent,
    DateFormatValidatorDirective,
    ContestLengthValidatorDirective,
    IndexToCharCodePipe,
    EditTeamsComponent,
    EditUserComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    AppRouteModule,
    DragulaModule,
    ReactiveFormsModule
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
