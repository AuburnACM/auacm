import { AppRouteModule } from './app-route/app-route.module';
import { BrowserModule } from '@angular/platform-browser';
import { DragulaModule } from 'ng2-dragula';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { BlogListComponent } from './blog-list/blog-list.component';
import { BlogPostComponent } from './blog-post/blog-post.component';
import { CompetitionsComponent } from './competitions/competitions.component';
import { CreateBlogComponent } from './create-blog/create-blog.component';
import { CreateUserComponent } from './create-user/create-user.component';
import { EditBlogComponent } from './edit-blog/edit-blog.component';
import { EditCompetitionComponent } from './edit-competition/edit-competition.component';
import { EditProblemComponent } from './edit-problem/edit-problem.component';
import { EditProfilePictureComponent } from './edit-profile-picture/edit-profile-picture.component';
import { EditTeamsComponent } from './edit-teams/edit-teams.component';
import { EditUserComponent } from './edit-user/edit-user.component';
import { JudgeComponent } from './judge/judge.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { ProblemsComponent } from './problems/problems.component';
import { ProfilePageComponent } from './profile-page/profile-page.component';
import { RankingComponent } from './ranking/ranking.component';
import { ViewProblemComponent } from './view-problem/view-problem.component';
import { ViewScoreboardComponent } from './view-scoreboard/view-scoreboard.component';

import { BlogService } from './blog.service';
import { CompetitionService } from './competition.service';
import { ProblemService } from './problem.service';
import { ProfileService } from './profile.service';
import { SubmissionService } from './submission.service';
import { UserService } from './user.service';
import { WebsocketService } from './websocket.service';

import { IndexToCharCodePipe } from './pipes/index-to-char-code.pipe';
import { LimitWordsPipe } from './pipes/limit-words.pipe';
import { MapKeysPipe } from './pipes/map-keys.pipe';
import { OrderByPipe } from './pipes/order-by.pipe';
import { SearchFilterPipe } from './pipes/search-filter.pipe';
import { SecondsToDateTimePipe } from './pipes/seconds-to-date-time.pipe';
import { SecondsToHoursPipe } from './pipes/seconds-to-hours.pipe';

import { ContestLengthValidatorDirective } from './directives/contest-length-validator.directive';
import { DateFormatValidatorDirective } from './directives/date-format-validator.directive';
import { ProblemValidatorDirective } from './directives/problem-validator.directive';


@NgModule({
  declarations: [
    AppComponent,
    BlogListComponent,
    BlogPostComponent,
    CompetitionsComponent,
    ContestLengthValidatorDirective,
    CreateBlogComponent,
    CreateUserComponent,
    DateFormatValidatorDirective,
    EditBlogComponent,
    EditCompetitionComponent,
    EditProblemComponent,
    EditProfilePictureComponent,
    EditTeamsComponent,
    EditUserComponent,
    IndexToCharCodePipe,
    JudgeComponent,
    LimitWordsPipe,
    MapKeysPipe,
    NotFoundComponent,
    OrderByPipe,
    ProblemsComponent,
    ProblemValidatorDirective,
    ProfilePageComponent,
    RankingComponent,
    SearchFilterPipe,
    SecondsToDateTimePipe,
    SecondsToHoursPipe,
    ViewProblemComponent,
    ViewScoreboardComponent
  ],
  imports: [
    AppRouteModule,
    BrowserModule,
    DragulaModule,
    FormsModule,
    HttpClientModule,
    HttpModule,
    ReactiveFormsModule
  ],
  providers: [
    BlogService,
    CompetitionService,
    ProblemService,
    ProfileService,
    SubmissionService,
    UserService,
    WebsocketService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
