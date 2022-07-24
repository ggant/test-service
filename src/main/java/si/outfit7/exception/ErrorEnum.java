package si.outfit7.exception;

public enum ErrorEnum {
    UNKNOWN_ERROR,
    INPUT_DATA_ERROR,
    DATABASE_ERROR,
    EXTERNAL_SERVICE_ERROR;

    private ErrorEnum() {
    }
}
