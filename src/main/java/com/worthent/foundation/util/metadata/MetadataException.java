package com.worthent.foundation.util.metadata;

/**
 * Thrown when an operation on a <code>DataType</code> or
 * <code>PropertyValue</code> fails.
 *
 * @author  Erik K. Worth
 */
public class MetadataException extends RuntimeException {
    
    /** Serial ID */
    private static final long serialVersionUID = -4682507569147861082L;

    /**
     * Constructs a new exception with the specified detail message. The cause
     * is not initialized, and may subsequently be initialized by a call to
     * {@link #initCause}.
     * 
     * @param message the detail message. The detail message is saved for later
     *            retrieval by the {@link #getMessage()} method.
     */
    public MetadataException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * <p>
     * Note that the detail message associated with <code>cause</code> is
     * <i>not</i> automatically incorporated in this exception's detail message.
     * 
     * @param message the detail message (which is saved for later retrieval by
     *            the {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A <tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public MetadataException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
