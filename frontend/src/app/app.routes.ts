import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { TaskList } from './pages/task-list/task-list';
import { TaskForm } from './pages/task-form/task-form';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'tasks', component: TaskList },
  { path: 'tasks/new', component: TaskForm },
  { path: 'tasks/:id/edit', component: TaskForm },
  { path: '**', redirectTo: 'login' }
];