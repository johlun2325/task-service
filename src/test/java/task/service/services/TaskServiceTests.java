package task.service.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import task.service.models.Task;
import task.service.repos.TaskRepository;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class TaskServiceTests
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
    void getAllTasksByUser()
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
    void getCompletedTasksByUser()
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
    void getPriorityTasksByUser()
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
}
