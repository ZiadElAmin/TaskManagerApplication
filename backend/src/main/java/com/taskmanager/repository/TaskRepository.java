package com.taskmanager.repository;

import com.taskmanager.entity.Task;
import com.taskmanager.entity.TaskPriority;
import com.taskmanager.entity.TaskStatus;
import com.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Basic listing, scoped to the owner so users only ever see their own tasks
    List<Task> findByOwner(User owner);

    List<Task> findByOwnerAndStatus(User owner, TaskStatus status);

    List<Task> findByOwnerAndPriority(User owner, TaskPriority priority);

    List<Task> findByOwnerAndStatusAndPriority(User owner, TaskStatus status, TaskPriority priority);

    Optional<Task> findByIdAndOwner(Long id, User owner);
}
