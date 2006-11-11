package net.sf.pzfilereader.converter;

/**
 * Exception thrown when a conversion error occurs
 * 
 * @author Paul Zepernick
 */
public class PZConvertException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public PZConvertException() {
    }

    /**
     * @param message
     */
    public PZConvertException(final String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public PZConvertException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public PZConvertException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
}
