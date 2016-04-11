import javax.ws.rs.NotFoundException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<javax.ws.rs.NotFoundException> {
    @Produces(MediaType.TEXT_HTML)
    public Response toResponse(NotFoundException exception) {
        String answer;
        
        answer = "<html><head><title>404 NOT FOUND</title></head><body><h1>404 RESOURCE NOT FOUND</h1></body></html>";
        
        return Response.status(Status.NOT_FOUND).entity(answer).build();
    }
}
