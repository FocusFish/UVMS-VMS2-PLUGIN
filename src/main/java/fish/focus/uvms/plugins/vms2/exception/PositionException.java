package fish.focus.uvms.plugins.vms2.exception;

import fish.focus.uvms.plugins.vms2.gen.model.ErrorResponse;

import java.util.UUID;

public class PositionException extends RuntimeException {
    private final transient ErrorResponse errorResponse;

    public PositionException(String errorMessage) {
        errorResponse = new ErrorResponse()
                .code(400)
                .detail(errorMessage)
                .id(UUID.randomUUID().toString());
    }

    public PositionException(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
