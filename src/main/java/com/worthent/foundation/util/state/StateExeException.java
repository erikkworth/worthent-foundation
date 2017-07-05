package com.worthent.foundation.util.state;

/**
 * Thrown when there is an error when processing an event submitted to the
 * state table.
 * 
 * @author Erik K. Worth
 */
public class StateExeException extends RuntimeException {
    
    /** Serial ID */
    private static final long serialVersionUID = -5472680390119244151L;

    /**
     * Constructs a new exception with the specified detail message. The cause
     * is not initialized, and may subsequently be initialized by a call to
     * {@link #initCause}.
     * 
     * @param message the detail message. The detail message is saved for later
     *        retrieval by the {@link #getMessage()} method.
     */
    public StateExeException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * <p>
     * Note that the detail message associated with <code>cause</code> is
     * <i>not</i> automatically incorporated in this exception's detail message.
     * 
     * @param message the detail message (which is saved for later retrieval by
     *        the {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the
     *        {@link #getCause()} method). (A <tt>null</tt> value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     */
    public StateExeException(final String message, final Exception cause) {
        super(message, cause);
    }
}
