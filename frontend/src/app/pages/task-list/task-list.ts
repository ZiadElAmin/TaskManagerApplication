import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TaskService } from '../../services/task.service';
import { AuthService } from '../../services/auth.service';
import { Task, TaskPriority, TaskStatus } from '../../models/task.model';

@Component({
  selector: 'app-task-list',
  imports: [
    ReactiveFormsModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule
  ],
  templateUrl: './task-list.html',
  styleUrl: './task-list.css'
})
export class TaskList implements OnInit {
  tasks: Task[] = [];
  loading = false;
  filterForm: FormGroup;

  statuses: TaskStatus[] = ['TODO', 'IN_PROGRESS', 'DONE'];
  priorities: TaskPriority[] = ['LOW', 'MEDIUM', 'HIGH'];

  constructor(
    private fb: FormBuilder,
    private taskService: TaskService,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.filterForm = this.fb.group({
      status: [''],
      priority: ['']
    });
  }

  ngOnInit(): void {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }

    this.loadTasks();
    this.filterForm.valueChanges.subscribe(() => this.loadTasks());
  }

  loadTasks(): void {
    this.loading = true;
    const { status, priority } = this.filterForm.getRawValue();

    this.taskService
      .getTasks((status as TaskStatus) || '', (priority as TaskPriority) || '')
      .subscribe({
        next: (tasks) => {
          this.tasks = tasks;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
          this.snackBar.open('Couldnt load tasks', 'Close', { duration: 3000 });
        }
      });
  }

  createTask(): void {
    this.router.navigate(['/tasks/new']);
  }

  editTask(id: number): void {
    this.router.navigate(['/tasks', id, 'edit']);
  }

  deleteTask(id: number): void {
    if (!confirm('Delete this task?')) return;

    this.taskService.deleteTask(id).subscribe({
      next: () => {
        this.snackBar.open('Task is deleted', 'Close', { duration: 2500 });
        this.loadTasks();
      },
      error: () => this.snackBar.open('Couldnt delete task', 'Close', { duration: 3000 })
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}