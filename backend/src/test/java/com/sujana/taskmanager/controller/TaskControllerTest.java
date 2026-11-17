
package com.sujana.taskmanager.controller;

import com.sujana.taskmanager.dto.TaskRequest;
import com.sujana.taskmanager.model.Task;
import com.sujana.taskmanager.model.TaskPriority;
import com.sujana.taskmanager.model.TaskStatus;
import com.sujana.taskmanager.service.TaskService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    com.fasterxml.jackson.databind.ObjectMapper mapper;

    @MockitoBean
    TaskService service;

    @Test
    void createTaskReturns201() throws Exception {

        TaskRequest request = new TaskRequest();
        request.setTitle("Prepare interview");
        request.setStatus(TaskStatus.TODO);
        request.setPriority(TaskPriority.HIGH);

        when(service.createTask(any())).thenAnswer(inv -> {
            Task task = new Task();
            task.setId(1L);
            task.setTitle("Prepare interview");
            task.setStatus(TaskStatus.TODO);
            task.setPriority(TaskPriority.HIGH);
            return task;
        });

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title")
                        .value("Prepare interview"));
    }

    @Test
    void blankTitleReturns400() throws Exception {

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"  \"}"))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getAllTasksReturns200() throws Exception {

        Task task = new Task();
        task.setId(1L);
        task.setTitle("Prepare interview");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.HIGH);

        when(service.getTasks(null, null)).thenReturn(List.of(task));

        mockMvc.perform(get("/api/tasks"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title")
                        .value("Prepare interview"));
    }
}
