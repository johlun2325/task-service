package task.service.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import task.service.models.Task;
import task.service.models.payloads.TaskPayload;
import task.service.repos.TaskRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest
{
    private TaskService taskService;

    @Mock
    TaskRepository repository;

    private AutoCloseable mocks;

    @BeforeEach
    void setup()
    {
        mocks = MockitoAnnotations.openMocks(this);
        taskService = new TaskService();
        taskService.repository = repository;
    }

    @AfterEach
    void tearDown() throws Exception
    {
        mocks.close();
    }

    @Test
    void getAllTasksByUser_ShouldCallRepository()
    {
        // Arrange
        var userUid = "user-123";
        List<Task> tasks = new ArrayList<>();
        when(repository.findByUserUid(userUid)).thenReturn(tasks);

        // Act
        taskService.getAllTasks(userUid);

        // Assert
        verify(repository, times(1)).findByUserUid(userUid);
        verify(repository, never()).findByUserUid(argThat(uid -> !uid.equals(userUid)));
    }

    @Test
    void getCompletedTasksByUser_ShouldCallRepository()
    {
        // Arrange
        var userUid = "user-123";
        List<Task> tasks = new ArrayList<>();
        when(repository.findCompletedByUserUid(userUid)).thenReturn(tasks);

        // Act
        taskService.getCompletedTasks(userUid);

        // Assert
        verify(repository, times(1)).findCompletedByUserUid(userUid);
        verify(repository, never()).findCompletedByUserUid(argThat(uid -> !uid.equals(userUid)));
    }

    @Test
    void getPriorityTasksByUser_ShouldCallRepository()
    {
        // Arrange
        var userUid = "user-123";
        List<Task> tasks = new ArrayList<>();
        when(repository.findPriorityByUserUid(userUid)).thenReturn(tasks);

        // Act
        taskService.getPriorityTasks(userUid);

        // Assert
        verify(repository, times(1)).findPriorityByUserUid(userUid);
        verify(repository, never()).findPriorityByUserUid(argThat(uid -> !uid.equals(userUid)));
    }

    @Test
    void createTask_ShouldPersistAndReturnTask()
    {
        // Arrange
        var userUid = "user-123";
        var payload = new TaskPayload();
        payload.setTitle("Test");
        payload.setDescription("Desc");
        payload.setPriority(true);
        payload.setCompleted(false);

        doAnswer(invocation -> null).when(repository).persist(any(Task.class));

        when(repository.findByUid(anyString())).thenAnswer(invocation -> {
            var task = new Task();
            task.setUid(invocation.getArgument(0));
            task.setTitle("Test");
            return task;
        });

        // Act
        var createdTask = taskService.createTask(userUid, payload);

        // Assert
        assertNotNull(createdTask.getUid());
        assertEquals("task", createdTask.getType());
        assertEquals(userUid, createdTask.getUserUid());
        assertEquals("Test", createdTask.getTitle());
        verify(repository, times(1)).persist(any(Task.class));
        verify(repository, times(1)).findByUid(eq(createdTask.getUid()));
    }

    @Test
    void updateTask_ShouldUpdateExistingTask()
    {
        // Arrange
        var itemUid = "task-123";
        var existingTask = new Task();
        existingTask.setUid(itemUid);
        existingTask.setTitle("Old title");
        existingTask.setDescription("Desc");
        existingTask.setCompleted(false);

        var payload = new TaskPayload();
        payload.setTitle("New title");
        payload.setCompleted(true);

        when(repository.findByUid(itemUid)).thenReturn(existingTask);

        // Act
        var updatedTask = taskService.updateTask(payload, itemUid);

        // Assert
        assertEquals("New title", updatedTask.getTitle());
        assertTrue(updatedTask.isCompleted());
        assertNotNull(updatedTask.getCompletedAt());
        verify(repository, times(1)).update(any(Task.class));
    }

    @Test
    void deleteTask_ShouldThrowIfNotFound()
    {
        // Arrange
        var uid = "task-123";
        when(repository.findByUid(uid)).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.deleteTask(uid));
        verify(repository, never()).delete((Task) any());
    }
}
