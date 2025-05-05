package task.service.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Data;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonRepresentation;

@Data
@MongoEntity(collection = "notes")
public final class Note
{
    @JsonIgnore
    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
    private String id;

    private String uid;
    private String userUid;
    private String type;
    private String title;
    private String text;
    private Long createdAt;
    private Long updatedAt;

    @Override
    public String toString()
    {
        return "Note{" + "\nuid='" + uid + '\'' + "\n, userUid='" + userUid + '\'' + "\n, title='" + title + '\''
                + "\n, text='" + text + '\'' + "\n, createdAt=" + createdAt + "\n, updatedAt=" + updatedAt + '}';
    }
}
