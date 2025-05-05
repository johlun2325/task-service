package task.service.repos;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;
import task.service.models.Task;

import java.util.List;

@ApplicationScoped
public class TaskRepository implements PanacheMongoRepository<Task>
{
    public Task findById(String id)
    {
        return findById(new ObjectId(id));
    }

    public Task findByUid(String uid)
    {
        return find("uid", uid).firstResult();
    }

    public List<Task> findByUserUid(String userUid)
    {
        return list("userUid", userUid);
    }

    public List<Task> findCompletedByUserUid(String userUid)
    {
        return list("userUid = ?1 and completed = ?2", userUid, true);
    }

    public List<Task> findPriorityByUserUid(String userUid)
    {
        return list("userUid = ?1 and priority = ?2", userUid, true);
    }
}
