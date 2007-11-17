package net.sf.flatpack.util;

/**
 * Thrown when using a parsing option inappropriately 
 * 
 * @author Paul Zepernick
 */
public class FPInvalidUsageException extends RuntimeException{
    
    public FPInvalidUsageException(final String msg) {
        super(msg);
    }
}
