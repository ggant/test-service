package si.outfit7.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.outfit7.exception.ErrorEnum;
import si.outfit7.exception.Outfit7Exception;

public class Outfit7ExceptionUtil {

    private static final Logger LOG = LoggerFactory.getLogger(Outfit7ExceptionUtil.class.getName());

    public static void throwException(ErrorEnum errorEnum, String code, String description, Throwable t) throws Outfit7Exception {
        LOG.error("Error {}{}{}{}", errorEnum.toString(), code != null ? ". Error code: " + code : "", description != null ? ". Error description: " + description : "", t != null ? " Cause: " : "", t);
        throw new Outfit7Exception(errorEnum, code, description, t);
    }

    public static void throwException(ErrorEnum errorEnum, String code, String description) throws Outfit7Exception {
        LOG.error("Error {}{}{}{}", errorEnum.toString(), code != null ? ". Error code: " + code : "", description != null ? ". Error description: " + description : "");
        throw new Outfit7Exception(errorEnum, code, description);
    }
}
