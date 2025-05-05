package task.service.repos;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;
import task.service.models.Task;

import java.util.List;

@ApplicationScoped
public class TaskRepository implements PanacheMongoRepository<Task>
{
    public Task findById(final String id)
    {
        return findById(new ObjectId(id));
    }

    public Task findByUid(final String uid)
    {
        return find("uid", uid).firstResult();
    }

    public List<Task> findByUserUid(final String userUid)
    {
        return list("userUid", userUid);
    }

    public List<Task> findCompletedByUserUid(final String userUid)
    {
        return list("userUid = ?1 and completed = ?2", userUid, true);
    }

    public List<Task> findPriorityByUserUid(final String userUid)
    {
        return list("userUid = ?1 and priority = ?2", userUid, true);
    }
}
