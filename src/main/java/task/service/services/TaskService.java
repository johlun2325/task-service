package task.service.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import task.service.models.Task;
import task.service.models.payloads.TaskPayload;
import task.service.repos.TaskRepository;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TaskService
{
    private final static Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    @Inject
    TaskRepository repository;

    public List<Task> getAllTasks(final String userUid)
    {
        LOGGER.debug("Fetching all tasks for user");
        var tasks = repository.findByUserUid(userUid);

        LOGGER.debug("All tasks fetched");
        return tasks;
    }

    public List<Task> getCompletedTasks(final String userUid)
    {
        LOGGER.debug("Fetching all completed tasks for user");
        var tasks = repository.findCompletedByUserUid(userUid);

        LOGGER.debug("All competed tasks fetched");
        return tasks;
    }

    public List<Task> getPriorityTasks(final String userUid)
    {
        LOGGER.debug("Fetching all priority tasks for user");
        var tasks = repository.findPriorityByUserUid(userUid);

        LOGGER.debug("All priority tasks fetched");
        return tasks;
    }

    public Task createTask(final String userUid, final TaskPayload payload)
    {

        LOGGER.info("Creating task");

        var task = buildNewTask(payload, userUid);

        repository.persist(task);

        LOGGER.info("Task persisted with values: {}", task);

        // for debugging fetch the task by uid, the same as eventUid
        var fetchedTask = repository.findByUid(task.getUid());
        LOGGER.info("Task fetched successfully with title: {}", fetchedTask.getTitle());

        return task;
    }

    public Task updateTask(final TaskPayload payload, final String itemUid)
    {
        LOGGER.info("Updating task");
        var taskToUpdate = repository.findByUid(itemUid);

        var task = buildUpdatedTask(payload, taskToUpdate);

        repository.update(task);

        LOGGER.info("Task updated with values: {}", task);

        return task;
    }

    public void deleteTask(final String itemUid)
    {
        try
        {
            var taskToDelete = repository.findByUid(itemUid);

            if (taskToDelete == null)
            {
                throw new NotFoundException("Task with uid " + itemUid + " not found");
            }

            repository.delete(taskToDelete);
            LOGGER.info("Task with uid {} deleted", itemUid);

        } catch (Exception e)
        {
            LOGGER.info("Could not delete task");
            throw new RuntimeException();
        }
    }

    private Task buildNewTask(final TaskPayload payload, final String userUid)
    {
        var task = new Task();

        // mongodb is generating id field
        task.setUid(UUID.randomUUID().toString()); // set unique uid for task
        task.setUserUid(userUid);
        task.setType("task");
        task.setTitle(payload.getTitle());
        task.setDescription(payload.getDescription());

        task.setPriority(payload.getPriority());
        task.setCompleted(payload.getCompleted());

        // when crated these values are equal, completed is null
        var currentTime = System.currentTimeMillis();
        task.setCreatedAt(currentTime);
        task.setUpdatedAt(currentTime);

        if (task.isCompleted())
        {
            task.setCompletedAt(currentTime);
        } else
        {
            task.setCompletedAt(null);
        }

        return task;
    }

    private Task buildUpdatedTask(final TaskPayload payload, final Task existing)
    {
        var currentTime = System.currentTimeMillis();

        if (payload.getTitle() != null)
            existing.setTitle(payload.getTitle());

        if (payload.getDescription() != null)
            existing.setDescription(payload.getDescription());

        if (payload.getPriority() != null)
            existing.setPriority(payload.getPriority());

        if (payload.getCompleted() != null)
        {
            var isCompleted = payload.getCompleted();

            existing.setCompleted(isCompleted);

            if (isCompleted && existing.getCompletedAt() == null)
            {
                // setting current time when completed if updated value=true and current is null
                existing.setCompletedAt(currentTime);

            } else if (!isCompleted)
            {
                existing.setCompletedAt(null);
            }
        }

        existing.setUpdatedAt(currentTime); // setting the current time when updated

        return existing;
    }
}
