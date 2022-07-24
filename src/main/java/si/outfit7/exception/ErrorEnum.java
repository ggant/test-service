package si.outfit7.exception;

/**
 * Custom error types in application
 * @author Goran Corkovic
 */
public enum ErrorEnum {
    UNKNOWN_ERROR,
    INPUT_DATA_ERROR,
    DATABASE_ERROR,
    EXTERNAL_SERVICE_ERROR;

    private ErrorEnum() {
    }
}
