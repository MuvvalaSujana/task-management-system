
package com.sujana.taskmanager.service;

import com.sujana.taskmanager.dto.TaskRequest;
import com.sujana.taskmanager.exception.ResourceNotFoundException;
import com.sujana.taskmanager.model.Task;
import com.sujana.taskmanager.model.TaskPriority;
import com.sujana.taskmanager.model.TaskStatus;
import com.sujana.taskmanager.repository.TaskRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    @Test
    void getTasksReturnsTaskList() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Prepare interview");

        when(repository.searchTasks(null, null))
                .thenReturn(List.of(task));

        List<Task> result = service.getTasks(null, null);

        assertEquals(1, result.size());
        assertEquals("Prepare interview", result.get(0).getTitle());
    }

    @Test
    void getTaskReturnsTaskWhenFound() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Prepare interview");

        when(repository.findById(1L))
                .thenReturn(Optional.of(task));

        Task result = service.getTask(1L);

        assertEquals(1L, result.getId());
        assertEquals("Prepare interview", result.getTitle());
    }

    @Test
    void getTaskThrowsExceptionWhenNotFound() {
        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getTask(99L));
    }

    @Test
    void createTaskSavesTask() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Complete assignment");
        request.setDescription("Practice unit testing");
        request.setStatus(TaskStatus.TODO);
        request.setPriority(TaskPriority.HIGH);

        when(repository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Task result = service.createTask(request);

        assertEquals("Complete assignment", result.getTitle());
        assertEquals("Practice unit testing", result.getDescription());
        assertEquals(TaskStatus.TODO, result.getStatus());
        assertEquals(TaskPriority.HIGH, result.getPriority());

        verify(repository).save(any(Task.class));
    }

    @Test
    void updateTaskUpdatesExistingTask() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Old title");

        TaskRequest request = new TaskRequest();
        request.setTitle("Updated title");
        request.setStatus(TaskStatus.IN_PROGRESS);
        request.setPriority(TaskPriority.HIGH);

        when(repository.findById(1L))
                .thenReturn(Optional.of(existingTask));

        when(repository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Task result = service.updateTask(1L, request);

        assertEquals("Updated title", result.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        assertEquals(TaskPriority.HIGH, result.getPriority());
    }

    @Test
    void deleteTaskDeletesExistingTask() {
        Task task = new Task();
        task.setId(1L);

        when(repository.findById(1L))
                .thenReturn(Optional.of(task));

        service.deleteTask(1L);

        verify(repository).delete(task);
    }
}
