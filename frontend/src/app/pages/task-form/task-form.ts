import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TaskService } from '../../services/task.service';
import { TaskPriority, TaskStatus } from '../../models/task.model';

@Component({
  selector: 'app-task-form',
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatCardModule
  ],
  templateUrl: './task-form.html',
  styleUrl: './task-form.css'
})
export class TaskForm implements OnInit {
  taskForm: FormGroup;
  statuses: TaskStatus[] = ['TODO', 'IN_PROGRESS', 'DONE'];
  priorities: TaskPriority[] = ['LOW', 'MEDIUM', 'HIGH'];

  isEditMode = false;
  private taskId: number | null = null;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private taskService: TaskService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.taskForm = this.fb.group({
      title: ['', Validators.required],
      description: [''],
      status: ['TODO' as TaskStatus, Validators.required],
      priority: ['MEDIUM' as TaskPriority, Validators.required]
    });
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam) {
      this.isEditMode = true;
      this.taskId = Number(idParam);
      this.loadTask(this.taskId);
    }
  }

  private loadTask(id: number): void {
    this.loading = true;
    this.taskService.getTaskById(id).subscribe({
      next: (task) => {
        this.taskForm.patchValue({
          title: task.title,
          description: task.description,
          status: task.status,
          priority: task.priority
        });
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.snackBar.open('Couldnt load el task', 'Close', { duration: 3000 });
        this.router.navigate(['/tasks']);
      }
    });
  }

  onSubmit(): void {
    if (this.taskForm.invalid) return;

    const payload = {
      title: this.taskForm.value.title!,
      description: this.taskForm.value.description ?? '',
      status: this.taskForm.value.status!,
      priority: this.taskForm.value.priority!
    };

    const request = this.isEditMode
      ? this.taskService.updateTask(this.taskId!, payload)
      : this.taskService.createTask(payload);

    request.subscribe({
      next: () => {
        this.snackBar.open(this.isEditMode ? 'Task updated' : 'Task created', 'Close', {
          duration: 2500
        });
        this.router.navigate(['/tasks']);
      },
      error: () => this.snackBar.open('error', 'Close', { duration: 3000 })
    });
  }

  cancel(): void {
    this.router.navigate(['/tasks']);
  }
}