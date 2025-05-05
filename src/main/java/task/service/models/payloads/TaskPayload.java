package task.service.models.payloads;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class TaskPayload
{
    public TaskPayload(final String title, final String description, final boolean priority, final boolean completed)
    {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.completed = completed;
    }

    private String title;
    private String description;
    private Boolean priority;
    private Boolean completed;
}
