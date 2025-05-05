package task.service.repos;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;
import task.service.models.Note;

import java.util.List;

@ApplicationScoped
public class NoteRepository implements PanacheMongoRepository<Note>
{
    public Note findById(final String id)
    {
        return findById(new ObjectId(id));
    }

    public Note findByUid(final String uid)
    {
        return find("uid", uid).firstResult();
    }

    public List<Note> findByUserUid(final String userUid)
    {
        return list("userUid", userUid);
    }
}
