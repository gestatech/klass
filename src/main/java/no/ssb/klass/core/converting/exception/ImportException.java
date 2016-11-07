package no.ssb.klass.core.converting.exception;

/**
 * @author Mads Lundemo, SSB.
 */
public class ImportException extends Exception {
    public ImportException(String message) {
        super(message);
    }

    public ImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
