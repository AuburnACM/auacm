import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes }  from '@angular/router';

import { AppComponent } from '../app.component';
import { BlogListComponent } from '../blog-list/blog-list.component';
import { ProblemsComponent } from '../problems/problems.component';
import { RankingComponent } from '../ranking/ranking.component';
import { CompetitionsComponent } from '../competitions/competitions.component';
import { JudgeComponent } from '../judge/judge.component';

const appRoutes: Routes = [
  { path: '', component: BlogListComponent },
  { path: 'problems', component: ProblemsComponent },
  { path: 'ranking', component: RankingComponent },
  { path: 'competitions', component: CompetitionsComponent },
  { path: 'judge/:problem', component: JudgeComponent },
  { path: 'judge', component: JudgeComponent }
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
