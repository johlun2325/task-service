package task.service.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import task.service.models.Note;
import task.service.models.payloads.NotePayload;
import task.service.services.NoteService;
import task.service.utils.ResponseUtils;

import java.util.List;

@Path("/note")
public class NoteResource
{
    private final static Logger LOGGER = LoggerFactory.getLogger(NoteResource.class);

    @Inject
    NoteService noteService;

    @GET
    @Path("/all/{userUid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response allNotesByUser(@PathParam("userUid") final String userUid)
    {
        LOGGER.debug("Get request for all notes received");

        if (userUid == null)
        {
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "User uid is required");
        }

        try
        {
            var notes = noteService.getAllNotes(userUid);
            return ResponseUtils.successResponse(Response.Status.OK, notes);
        } catch (final Exception e)
        {
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "Could not fetch notes");
        }
    }

    @POST
    @Path("/create/{userUid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@PathParam("userUid") final String userUid, final NotePayload payload)
    {
        LOGGER.debug("Create request received for note item");

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
            var note = noteService.createNote(payload, userUid);
            return ResponseUtils.successResponse(Response.Status.CREATED, note);

        } catch (final Exception e)
        {
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "Could not create note");
        }
    }

    @PUT
    @Path("/update/{itemUid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("itemUid") final String itemUid, final NotePayload payload)
    {
        LOGGER.debug("Update request received for note item");

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
            var updatedNote = noteService.updateNote(payload, itemUid);
            return ResponseUtils.successResponse(Response.Status.OK, updatedNote);

        } catch (final Exception e)
        {
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "Could not update note");
        }
    }

    @DELETE
    @Path("/delete/{itemUid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("itemUid") final String itemUid)
    {
        LOGGER.debug("Delete request received for note item");

        if (itemUid == null)
        {
            return ResponseUtils.errorResponse(Response.Status.BAD_REQUEST, "Item uid is required");
        }

        try
        {
            noteService.deleteNote(itemUid);
            return ResponseUtils.successResponse(Response.Status.OK, "Note deleted successfully");

        } catch (final Exception e)
        {
            return ResponseUtils.errorResponse(Response.Status.NOT_FOUND, "Item could not be deleted");
        }
    }

    private boolean validatePayload(final NotePayload payload)
    {
        if (payload.getTitle().isEmpty())
            return false;

        if (payload.getText().isEmpty())
            return false;

        LOGGER.debug("Payload has valida values");
        return true;
    }
}
