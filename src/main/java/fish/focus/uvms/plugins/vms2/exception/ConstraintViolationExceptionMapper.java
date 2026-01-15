package fish.focus.uvms.plugins.vms2.exception;

import fish.focus.uvms.plugins.vms2.gen.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    private static final Logger LOG = LoggerFactory.getLogger(ConstraintViolationExceptionMapper.class);

    private static final int STATUS_CODE = 400;

    @Override
    public Response toResponse(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraints = ex.getConstraintViolations();
        LOG.error("Error in API request: {}", constraints);

        String details = constraints.stream()
                .map(constraintViolation -> {
                    var propertyPath = constraintViolation.getPropertyPath();
                    String name = null;
                    for (Path.Node node : propertyPath) {
                        name = node.getName();
                    }
                    return String.format("%s: %s", name, constraintViolation.getMessage());
                })
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = new ErrorResponse()
                .code(STATUS_CODE)
                .detail(details)
                .id(MDC.get("requestId"));

        return Response.status(STATUS_CODE)
                .entity(errorResponse)
                .header("Content-Type", "application/problem+json")
                .build();
    }
}
