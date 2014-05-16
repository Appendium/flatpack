package net.sf.flatpack.util;

/**
 * Thrown when using a parsing option inappropriately 
 * 
 * @author Paul Zepernick
 */
public class FPInvalidUsageException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public FPInvalidUsageException(final String msg) {
        super(msg);
    }
}
