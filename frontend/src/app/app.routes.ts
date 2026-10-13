
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
    path: 'tasks',
    loadComponent: () =>
      import('./features/task/pages/task').then(
        (m) => m.Tasks,
      ),
  },
  
{
  path: 'projects/:projectId/tasks',
  loadComponent: () =>
    import('./features/task/pages/task')
      .then(m => m.Tasks),
},

  {
    path: '**',
    redirectTo: 'login',
  },
];
