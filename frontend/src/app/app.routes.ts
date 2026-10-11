
import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login',
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/pages/login/login').then(
        (m) => m.Login,
      ),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/pages/register/register').then(
        (m) => m.Register,
      ),
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./features/dashboard/dashboard').then(
        (m) => m.Dashboard,
      ),
  },
  {
    path: 'organisations/create',
    loadComponent: () =>
      import(
        './features/organisation/pages/create-organisation/create-organisation'
      ).then((m) => m.CreateOrganisation),
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
