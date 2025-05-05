package task.service.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import task.service.models.Note;
import task.service.models.payloads.NotePayload;
import task.service.repos.NoteRepository;

import java.util.UUID;

@ApplicationScoped
public class NoteService
{
    private final static Logger LOGGER = LoggerFactory.getLogger(NoteService.class);

    @Inject
    NoteRepository repository;

    public Note createNote(final NotePayload payload, final String userUid)
    {
        LOGGER.info("Creating note");

        var note = buildNewNote(payload, userUid);

        repository.persist(note);

        LOGGER.info("Note persisted with values: {}", note);

        // for debugging fetch the task by uid, the same as eventUid
        var fetchedNote = repository.findByUid(note.getUid());
        LOGGER.debug("Note fetched successfully with title: {}", fetchedNote.getTitle());

        return note;
    }

    public Note updateNote(final NotePayload payload, final String itemUid)
    {
        LOGGER.info("Updating note");

        var note = buildUpdatedNote(payload, itemUid);

        repository.update(note);

        LOGGER.info("Note updated with values: {}", note);

        var fetchedTask = repository.findByUid(note.getUid());
        LOGGER.debug("Updated note fetched successfully with title: {}", fetchedTask.getTitle());

        return note;
    }

    public void deleteNote(final String itemUid)
    {
        try
        {
            var noteToDelete = repository.findByUid(itemUid);

            if (noteToDelete == null)
            {
                throw new NotFoundException("Task with uid " + itemUid + " not found");
            }

            repository.delete(noteToDelete);
            LOGGER.debug("Task with uid {} deleted", itemUid);

        } catch (Exception e)
        {
            LOGGER.debug("Could not delete task");
            throw new RuntimeException();
        }
    }

    private Note buildUpdatedNote(final NotePayload payload, final String itemUid)
    {
        var noteToUpdate = repository.findByUid(itemUid);

        if (payload.getTitle() != null)
            noteToUpdate.setTitle(payload.getTitle());

        if (payload.getText() != null)
            noteToUpdate.setText(payload.getText());

        noteToUpdate.setUpdatedAt(System.currentTimeMillis()); // setting current time when updated

        return noteToUpdate;
    }

    private Note buildNewNote(final NotePayload payload, final String userUid)
    {
        var note = new Note();

        // mongodb generating id field
        note.setUid(UUID.randomUUID().toString()); // set unique uid for note
        note.setUserUid(userUid);
        note.setType("note");
        note.setTitle(payload.getTitle());
        note.setText(payload.getText());

        // when crated these values are equal, completed is null
        var currentTime = System.currentTimeMillis();
        note.setCreatedAt(currentTime);
        note.setUpdatedAt(currentTime);

        return note;
    }
}
