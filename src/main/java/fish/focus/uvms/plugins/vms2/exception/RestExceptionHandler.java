package fish.focus.uvms.plugins.vms2.exception;

import fish.focus.uvms.plugins.vms2.gen.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RestExceptionHandler implements ExceptionMapper<Exception> {
    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);

    @Override
    public Response toResponse(Exception ex) {
        LOG.error("Got error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = null;
        if (ex instanceof PositionException) {
            PositionException positionException = (PositionException) ex;
            errorResponse = positionException.getErrorResponse();
        }

        if (errorResponse == null) {
            errorResponse = new ErrorResponse()
                    .code(Status.INTERNAL_SERVER_ERROR.getStatusCode())
                    .detail(ex.getMessage())
                    .id(MDC.get("requestId"));
        }

        return Response.status(errorResponse.getCode())
                .entity(errorResponse)
                .header("Content-Type", "application/problem+json")
                .build();
    }
}
