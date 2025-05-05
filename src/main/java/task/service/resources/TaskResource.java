package task.service.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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
    public Response allNotesByUser(@PathParam("userUid") final String userUid)
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
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "Could not fetch tasks");
        }
    }

    @POST
    @Path("/create/{userUid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@PathParam("userUid") final String userUid, final TaskPayload payload)
    {
        LOGGER.debug("Create request received for task item");

        if (userUid == null)
        {
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "User uid is required");
        }

        if (!validatePayload(payload))
        {
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "Invalid payload");
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
    public Response update(@PathParam("itemUid") final String itemUid, final TaskPayload payload)
    {
        LOGGER.debug("Update request received for task item");

        if (itemUid == null)
        {
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "Item uid is required");
        }

        if (!validatePayload(payload))
        {
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "Invalid payload");
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

    private boolean validatePayload(final TaskPayload payload)
    {
        if (payload.getTitle().isEmpty())
            return false;

        if (payload.getDescription().isEmpty())
            return false;

        if (payload.getPriority() == null)
            return false;

        if (payload.getCompleted() == null)
            return false;

        LOGGER.debug("Payload has valida values");
        return true;
    }
}
