package task.service.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import task.service.models.Note;
import task.service.models.payloads.NotePayload;
import task.service.repos.NoteRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NoteServiceTest
{
    private NoteService noteService;

    @Mock
    NoteRepository repository;

    private AutoCloseable mocks;

    @BeforeEach
    void setup()
    {
        mocks = MockitoAnnotations.openMocks(this);
        noteService = new NoteService();
        noteService.repository = repository;
    }

    @AfterEach
    void tearDown() throws Exception
    {
        mocks.close();
    }

    @Test
    void getAllNotesByUser_ShouldCallRepository()
    {
        // Arrange
        var userUid = "user-123";
        List<Note> notes = new ArrayList<>();
        when(repository.findByUserUid(userUid)).thenReturn(notes);

        // Act
        noteService.getAllNotes(userUid);

        // Assert
        verify(repository, times(1)).findByUserUid(userUid);
        verify(repository, never()).findByUserUid(argThat(uid -> !uid.equals(userUid)));
    }

    @Test
    void createNote_ShouldPersistAndReturnNote()
    {
        // Arrange
        var userUid = "user-123";
        var payload = new NotePayload();
        payload.setTitle("Test Note");
        payload.setText("Some text");

        doAnswer(invocation -> null).when(repository).persist(any(Note.class));

        when(repository.findByUid(anyString())).thenAnswer(invocation -> {
            var note = new Note();
            note.setUid(invocation.getArgument(0));
            note.setTitle("Test Note");
            return note;
        });

        // Act
        var createdNote = noteService.createNote(userUid, payload);

        // Assert
        assertNotNull(createdNote.getUid());
        assertEquals("note", createdNote.getType());
        assertEquals(userUid, createdNote.getUserUid());
        assertEquals("Test Note", createdNote.getTitle());
        verify(repository, times(1)).persist(any(Note.class));
        verify(repository, times(1)).findByUid(eq(createdNote.getUid()));
    }

    @Test
    void updateNote_ShouldUpdateExistingNote()
    {
        // Arrange
        var itemUid = "note-123";
        var existingNote = new Note();
        existingNote.setUid(itemUid);
        existingNote.setTitle("Old title");
        existingNote.setText("Old text");

        var payload = new NotePayload();
        payload.setTitle("New title");
        payload.setText("New text");

        when(repository.findByUid(itemUid)).thenReturn(existingNote);

        // Act
        var updatedNote = noteService.updateNote(payload, itemUid);

        // Assert
        assertEquals("New title", updatedNote.getTitle());
        assertEquals("New text", updatedNote.getText());
        assertNotNull(updatedNote.getUpdatedAt());
        assertNotEquals(updatedNote.getUpdatedAt(), updatedNote.getCreatedAt());
        verify(repository, times(1)).update(any(Note.class));
    }

    @Test
    void deleteNote_ShouldThrowIfNotFound()
    {
        // Arrange
        var uid = "note-123";
        when(repository.findByUid(uid)).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> noteService.deleteNote(uid));
        verify(repository, never()).delete((Note) any());
    }

    @Test
    void deleteNote_ShouldDeleteIfFound()
    {
        // Arrange
        var uid = "note-123";
        var note = new Note();
        note.setUid(uid);
        when(repository.findByUid(uid)).thenReturn(note);

        // Act
        noteService.deleteNote(uid);

        // Assert
        verify(repository, times(1)).delete(note);
    }
}
