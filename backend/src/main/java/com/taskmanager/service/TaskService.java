package com.taskmanager.service;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.TaskPriority;
import com.taskmanager.entity.TaskStatus;
import com.taskmanager.entity.User;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.repository.TaskRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository, CurrentUserProvider currentUserProvider) {
        this.taskRepository = taskRepository;
        this.currentUserProvider = currentUserProvider;
    }

    private final CurrentUserProvider currentUserProvider;

    public TaskResponse createTask(TaskRequest request) {
        User owner = currentUserProvider.getCurrentUser();

        Task task = new Task(
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getPriority(),
                owner
        );

        Task saved = taskRepository.save(task);
        return TaskResponse.fromEntity(saved);
    }

    // status and priority are optional filters - null means "don't filter by this"
    public List<TaskResponse> getTasks(TaskStatus status, TaskPriority priority) {
        User owner = currentUserProvider.getCurrentUser();

        List<Task> tasks;
        if (status != null && priority != null) {
            tasks = taskRepository.findByOwnerAndStatusAndPriority(owner, status, priority);
        } else if (status != null) {
            tasks = taskRepository.findByOwnerAndStatus(owner, status);
        } else if (priority != null) {
            tasks = taskRepository.findByOwnerAndPriority(owner, priority);
        } else {
            tasks = taskRepository.findByOwner(owner);
        }

        return tasks.stream().map(TaskResponse::fromEntity).toList();
    }

    public TaskResponse getTaskById(Long id) {
        Task task = findOwnedTaskOrThrow(id);
        return TaskResponse.fromEntity(task);
    }

    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = findOwnedTaskOrThrow(id);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        Task saved = taskRepository.save(task);
        return TaskResponse.fromEntity(saved);
    }

    public void deleteTask(Long id) {
        Task task = findOwnedTaskOrThrow(id);
        taskRepository.delete(task);
    }

    // Enforces "each user can only see and manage their own tasks":
    // we look the task up scoped to the owner, so a task belonging to
    // someone else simply doesn't exist as far as this user is concerned.
    private Task findOwnedTaskOrThrow(Long id) {
        User owner = currentUserProvider.getCurrentUser();
        return taskRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }
}
