/**
 * 
 */
package net.sf.pzfilereader;

/**
 * @author xhensevb
 *
 */
public class InitialisationException extends RuntimeException {
    private static final long serialVersionUID = -4181701730609348676L;

    /**
     * 
     */
    public InitialisationException() {
    }

    /**
     * @param message
     */
    public InitialisationException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public InitialisationException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public InitialisationException(String message, Throwable cause) {
        super(message, cause);
    }
}
