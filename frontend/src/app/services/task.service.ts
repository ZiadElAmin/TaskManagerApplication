import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, TaskRequest, TaskStatus, TaskPriority } from '../models/task.model';

const API_BASE = 'http://localhost:8080/api/tasks';

@Injectable({ providedIn: 'root' })
export class TaskService {
  constructor(private http: HttpClient) {}

  getTasks(status?: TaskStatus | '', priority?: TaskPriority | ''): Observable<Task[]> {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    if (priority) params = params.set('priority', priority);
    return this.http.get<Task[]>(API_BASE, { params });
  }

  getTaskById(id: number): Observable<Task> {
    return this.http.get<Task>(`${API_BASE}/${id}`);
  }

  createTask(task: TaskRequest): Observable<Task> {
    return this.http.post<Task>(API_BASE, task);
  }

  updateTask(id: number, task: TaskRequest): Observable<Task> {
    return this.http.put<Task>(`${API_BASE}/${id}`, task);
  }

  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE}/${id}`);
  }
}
