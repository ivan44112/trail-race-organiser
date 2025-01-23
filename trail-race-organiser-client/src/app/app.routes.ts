import {Routes} from '@angular/router';
import {LoginComponent} from './pages/login/login.component';
import {AdministratorDashboardComponent} from './pages/administrator-dashboard/administrator-dashboard.component';
import {ApplicantDashboardComponent} from './pages/applicant-dashboard/applicant-dashboard.component';

export const routes: Routes = [
  {path: '', redirectTo: 'login', pathMatch: 'full'},
  {path: 'login', component: LoginComponent},
  {path: 'administrator-dashboard', component: AdministratorDashboardComponent},
  {path: 'applicant-dashboard', component: ApplicantDashboardComponent},
];
