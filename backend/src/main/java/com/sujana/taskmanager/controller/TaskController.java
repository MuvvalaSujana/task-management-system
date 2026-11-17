package com.sujana.taskmanager.controller;

import com.sujana.taskmanager.dto.TaskRequest;
import com.sujana.taskmanager.model.Task;
import com.sujana.taskmanager.model.TaskStatus;
import com.sujana.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {
    private final TaskService service;
    public TaskController(TaskService service) { this.service = service; }

    @GetMapping
    public List<Task> getTasks(@RequestParam(required = false) TaskStatus status,
                               @RequestParam(required = false) String search) {
        return service.getTasks(status, search);
    }
    @GetMapping("/{id}") public Task getTask(@PathVariable Long id) { return service.getTask(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@Valid @RequestBody TaskRequest request) { return service.createTask(request); }
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return service.updateTask(id, request);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) { service.deleteTask(id); }
}
