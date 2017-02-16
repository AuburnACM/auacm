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

const appRoutes: Routes = [
  { path: '', component: BlogListComponent },
  { path: 'problems', component: ProblemsComponent },
  { path: 'rankings', component: RankingComponent },
  { path: 'competitions', component: CompetitionsComponent },
  { path: 'judge/:problem', component: JudgeComponent },
  { path: 'judge', component: JudgeComponent },
  { path: 'users/create', component: CreateUserComponent },
  { path: 'blogs/create', component: CreateBlogComponent },
  { path: 'blog/:id/edit', component: EditBlogComponent },
  { path: 'blog/:id', component: BlogPostComponent },
  { path: 'problem/:shortName', component: ViewProblemComponent }
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
