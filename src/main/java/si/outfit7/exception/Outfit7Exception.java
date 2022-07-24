package si.outfit7.exception;

/**
 * Custom application exception
 * @author Goran Corkovic
 */
public class Outfit7Exception extends Exception {
    private static final long serialVersionUID = 1L;
    private ErrorEnum errorEnum;
    private String code;

    public Outfit7Exception(ErrorEnum errorEnum, String code, String description, Throwable cause) {
        super(description, cause);
        this.errorEnum = errorEnum;
        this.code = code;
    }

    public Outfit7Exception(ErrorEnum errorEnum, String code, String description) {
        super(description);
        this.errorEnum = errorEnum;
        this.code = code;
    }

    public Outfit7Exception(Throwable cause) {
        super(cause);
        this.errorEnum = ErrorEnum.UNKNOWN_ERROR;
    }

    public ErrorEnum getError() {
        return errorEnum;
    }


    public String getCode() {
        return code;
    }
}
