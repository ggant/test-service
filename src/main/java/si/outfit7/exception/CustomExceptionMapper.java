package si.outfit7.exception;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom error handling and sending specific
 * responses back when an exception or error occurs.
 * @author Goran Corkovic
 */
@Provider
public class CustomExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = LoggerFactory.getLogger(CustomExceptionMapper.class);

    @Override
    @Produces({ MediaType.APPLICATION_JSON })
    public Response toResponse(Exception e) {

        /*
            Catch application error (Outfit7Exception).
            If something is wrong with input data than return HTTP 400 with custom code and description.
            For all other application errors (database isn't working, problems with external service,...)
            hide all data from user and show only predefined code.

            Other well know errors just forward back to client.

            Log runtime error and hide all information from client (return HTTP 500 with predefined code).
         */
        if (e instanceof Outfit7Exception) {
            Outfit7Error outfit7Error = null;
            if(((Outfit7Exception) e).getError().equals(ErrorEnum.INPUT_DATA_ERROR))
                outfit7Error = new Outfit7Error(Response.Status.BAD_REQUEST.getStatusCode(), ((Outfit7Exception) e).getCode(), e.getMessage());
            else
                outfit7Error = new Outfit7Error(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),"OUTFIT7-EXCEPTION", e.getMessage());

            return Response.status(outfit7Error.getStatus()).entity(outfit7Error).type(MediaType.APPLICATION_JSON).build();
        } else if (e instanceof BadRequestException) {

            Outfit7Error re = new Outfit7Error(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.toString(), e.getMessage());
            return Response.status(re.getStatus()).entity(re).type(MediaType.APPLICATION_JSON).build();

        } else if (e instanceof NotAuthorizedException) {

            Outfit7Error re = new Outfit7Error(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.toString(), e.getMessage());
            return Response.status(re.getStatus()).entity(re).type(MediaType.APPLICATION_JSON).build();

        } else if (e instanceof ForbiddenException) {

            Outfit7Error re = new Outfit7Error(Response.Status.FORBIDDEN.getStatusCode(), Response.Status.FORBIDDEN.toString(), e.getMessage());
            return Response.status(re.getStatus()).entity(re).type(MediaType.APPLICATION_JSON).build();

        } else if (e instanceof NotFoundException) {

            Outfit7Error re = new Outfit7Error(Response.Status.NOT_FOUND.getStatusCode(), Response.Status.NOT_FOUND.toString(), e.getMessage());
            return Response.status(re.getStatus()).entity(re).type(MediaType.APPLICATION_JSON).build();

        } else if (e instanceof NotAllowedException) {

            Outfit7Error re = new Outfit7Error(Response.Status.METHOD_NOT_ALLOWED.getStatusCode(), Response.Status.METHOD_NOT_ALLOWED.toString(), e.getMessage());
            return Response.status(re.getStatus()).entity(re).type(MediaType.APPLICATION_JSON).build();

        } else if (e instanceof NotAcceptableException) {

            Outfit7Error re = new Outfit7Error(Response.Status.NOT_ACCEPTABLE.getStatusCode(), Response.Status.NOT_ACCEPTABLE.toString(), e.getMessage());
            return Response.status(re.getStatus()).entity(re).type(MediaType.APPLICATION_JSON).build();

        } else if (e instanceof NotSupportedException) {

            Outfit7Error re = new Outfit7Error(Response.Status.HTTP_VERSION_NOT_SUPPORTED.getStatusCode(), Response.Status.HTTP_VERSION_NOT_SUPPORTED.toString(), e.getMessage());
            return Response.status(re.getStatus()).entity(re).type(MediaType.APPLICATION_JSON).build();

        } else if (e instanceof InternalServerErrorException) {

            Outfit7Error re = new Outfit7Error(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.toString(), e.getMessage());
            return Response.status(re.getStatus()).entity(re).type(MediaType.APPLICATION_JSON).build();

        } else if (e instanceof ServiceUnavailableException) {

            Outfit7Error re = new Outfit7Error(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(), Response.Status.SERVICE_UNAVAILABLE.toString(), e.getMessage());
            return Response.status(re.getStatus()).entity(re).type(MediaType.APPLICATION_JSON).build();

        } else {
            LOG.error("Runtime exception{}:", e);
            Outfit7Error re = new Outfit7Error(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "OUTFIT7-RUNTIME-01", "Runtime error");
            return Response.status(re.getStatus()).entity(re).build();
        }

    }
}
