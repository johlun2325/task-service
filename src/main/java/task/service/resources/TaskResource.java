package task.service.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import task.service.models.payloads.TaskPayload;
import task.service.services.TaskService;
import task.service.utils.ResponseUtils;

@Path("/task")
public class TaskResource
{
    private final static Logger LOGGER = LoggerFactory.getLogger(TaskResource.class);

    @Inject
    TaskService taskService;

    @GET
    @Path("/all/{userUid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all tasks by specified user")
    public Response allTasksByUser(@PathParam("userUid") final String userUid)
    {
        LOGGER.debug("Get request for all tasks received");

        if (userUid == null)
        {
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "User uid is required");
        }

        try
        {
            var tasks = taskService.getAllTasks(userUid);
            return ResponseUtils.successResponse(Response.Status.OK, tasks);

        } catch (final Exception e)
        {
            LOGGER.debug("Could not fetch tasks");
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "Could not fetch tasks");
        }
    }

    @GET
    @Path("/completed/{userUid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all completed tasks")
    public Response completedTasksByUser(@PathParam("userUid") final String userUid)
    {
        LOGGER.debug("Get request for all completed tasks received");

        if (userUid == null)
        {
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "User uid is required");
        }

        try
        {
            var tasks = taskService.getCompletedTasks(userUid);
            return ResponseUtils.successResponse(Response.Status.OK, tasks);
        } catch (final Exception e)
        {
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "Could not fetch tasks");
        }
    }

    @GET
    @Path("/priority/{userUid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all prioritized tasks")
    public Response priorityTasksByUser(@PathParam("userUid") final String userUid)
    {
        LOGGER.debug("Get request for all priority tasks received");

        if (userUid == null)
        {
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "User uid is required");
        }

        try
        {
            var tasks = taskService.getPriorityTasks(userUid);
            return ResponseUtils.successResponse(Response.Status.OK, tasks);

        } catch (final Exception e)
        {
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "Could not fetch tasks");
        }
    }
    @POST
    @Path("/create/{userUid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new task for user")
    public Response create(@PathParam("userUid") final String userUid, final TaskPayload payload)
    {
        LOGGER.debug("Create request received for task item");

        if (userUid == null)
        {
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "User uid is required");
        }

        if (!payload.isValidForCreate())
        {
            LOGGER.debug("Payload is invalid, cannot create task");
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST,
                    "Invalid payload, must send all fields to create");
        }

        try
        {
            var task = taskService.createTask(userUid, payload);
            return ResponseUtils.successResponse(Response.Status.CREATED, task);

        } catch (Exception e)
        {
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "Could not create task");
        }
    }

    @PUT
    @Path("/update/{itemUid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update specified task")
    public Response update(@PathParam("itemUid") final String itemUid, final TaskPayload payload)
    {
        LOGGER.debug("Update request received for task item");

        if (itemUid == null)
        {
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "Item uid is required");
        }

        if (!payload.isValidForUpdate())
        {
            LOGGER.debug("Payload is invalid, cannot update task");
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST,
                    "Invalid payload, must send minimum of one field to update");
        }

        try
        {
            var updatedTask = taskService.updateTask(payload, itemUid);
            return ResponseUtils.successResponse(Response.Status.OK, updatedTask);

        } catch (Exception e)
        {
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "Could not update task");
        }
    }

    @DELETE
    @Path("/delete/{itemUid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete specified task")
    public Response delete(@PathParam("itemUid") final String itemUid)
    {
        LOGGER.debug("Delete request received for task item");
        if (itemUid == null)
        {
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "Item uid is required");
        }

        try
        {
            taskService.deleteTask(itemUid);
            return ResponseUtils.successResponse(Response.Status.OK, "Task deleted successfully");

        } catch (Exception e)
        {
            return ResponseUtils.errorResponse(Response.Status.NOT_FOUND, "Item could not be deleted");
        }
    }
}
