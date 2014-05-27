package net.sf.flatpack.util;

/**
 * Generic exception for FlatPack
 *
 * @author Paul Zepernick
 */
public class FPException extends RuntimeException {
    private static final long serialVersionUID = -4269317129024968528L;

    public FPException() {
    }

    /**
     * @param message
     */
    public FPException(final String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public FPException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public FPException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
