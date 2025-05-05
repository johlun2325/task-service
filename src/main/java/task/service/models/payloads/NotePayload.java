package task.service.models.payloads;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NotePayload
{
    public NotePayload(final String title, final String text)
    {
        this.title = title;
        this.text = text;
    }

    private String title;
    private String text;
}
