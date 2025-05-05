package task.service.utils;

import jakarta.ws.rs.core.Response;

import java.util.Map;

public final class ResponseUtils
{
    ResponseUtils()
    {
    }

    public static Response errorResponse(Response.Status status, String message)
    {
        return Response.status(status)
                .entity(Map.of("status", "error", "code", status.getStatusCode(), "message", message)).build();
    }

    public static Response successResponse(Response.Status status, Object data)
    {
        return Response.status(status).entity(Map.of("status", "success", "code", status.getStatusCode(), "data", data))
                .build();
    }
}
