
package com.sujana.taskmanager.service;

import com.sujana.taskmanager.dto.TaskRequest;
import com.sujana.taskmanager.exception.ResourceNotFoundException;
import com.sujana.taskmanager.model.*;
import com.sujana.taskmanager.repository.TaskRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Task> getTasks(TaskStatus status, String search) {
        String term = (search == null || search.isBlank())
                ? null : search.trim();

        return repository.searchTasks(status, term);
    }

    @Transactional(readOnly = true)
    public Task getTask(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Task not found with id: " + id));
    }

    public Task createTask(TaskRequest request) {
        Task task = new Task();
        copy(request, task);
        return repository.save(task);
    }

    public Task updateTask(Long id, TaskRequest request) {
        Task task = getTask(id);
        copy(request, task);
        return repository.save(task);
    }

    public void deleteTask(Long id) {
        repository.delete(getTask(id));
    }

    private void copy(TaskRequest request, Task task) {
        task.setTitle(request.getTitle().trim());

        task.setDescription(
                request.getDescription() == null
                        ? "" : request.getDescription().trim());

        task.setStatus(
                request.getStatus() == null
                        ? TaskStatus.TODO : request.getStatus());

        task.setPriority(
                request.getPriority() == null
                        ? TaskPriority.MEDIUM : request.getPriority());

        task.setDueDate(request.getDueDate());
    }
}
