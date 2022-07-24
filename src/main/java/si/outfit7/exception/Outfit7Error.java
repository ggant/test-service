package si.outfit7.exception;

public class Outfit7Error {

    private static final long serialVersionUID = 1L;
    protected Integer status;
    protected String code;
    protected String description;

    public Integer getStatus() {
        return this.status;
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    public Outfit7Error() {
    }

    public Outfit7Error(Integer status, String code, String description) {
        this.status = status;
        this.code = code;
        this.description = description;
    }
}
