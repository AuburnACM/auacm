import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes }  from '@angular/router';

import { AppComponent } from '../app.component';
import { BlogListComponent } from '../blog-list/blog-list.component';
import { ProblemsComponent } from '../problems/problems.component';
import { RankingComponent } from '../ranking/ranking.component';
import { CompetitionsComponent } from '../competitions/competitions.component';
import { JudgeComponent } from '../judge/judge.component';
import { CreateUserComponent } from '../create-user/create-user.component';
import { BlogPostComponent } from '../blog-post/blog-post.component';
import { CreateBlogComponent } from '../create-blog/create-blog.component';
import { EditBlogComponent } from '../edit-blog/edit-blog.component';
import { ViewProblemComponent } from '../view-problem/view-problem.component';
import { EditProblemComponent } from '../edit-problem/edit-problem.component';
import { NotFoundComponent } from '../not-found/not-found.component';
import { ViewScoreboardComponent } from '../view-scoreboard/view-scoreboard.component';

const appRoutes: Routes = [
  { path: '', component: BlogListComponent },
  { path: 'problems', component: ProblemsComponent },
  { path: 'rankings', component: RankingComponent },
  { path: 'competitions', component: CompetitionsComponent },
  { path: 'competition/:cid', component: ViewScoreboardComponent },
  { path: 'judge/:problem', component: JudgeComponent },
  { path: 'judge', component: JudgeComponent },
  { path: 'users/create', component: CreateUserComponent },
  { path: 'blogs/create', component: CreateBlogComponent },
  { path: 'blog/:id/edit', component: EditBlogComponent },
  { path: 'blog/:id', component: BlogPostComponent },
  { path: 'problem/:pid/edit', component: EditProblemComponent },
  { path: 'problem/:shortName', component: ViewProblemComponent },
  { path: 'problems/create', component: EditProblemComponent },
  { path: '404', component: NotFoundComponent }
];

@NgModule({
  imports: [
    RouterModule.forRoot(appRoutes)
  ],
  exports: [
    RouterModule
  ]
})
export class AppRouteModule { }
