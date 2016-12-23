/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state.def;

/**
 * Thrown when there is an error or inconsistency in the state table definition.
 * 
 * @author Erik K. Worth
 * @version $Id: StateDefException.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
 */
public class StateDefException extends RuntimeException {
    
    /** Serial ID */
    private static final long serialVersionUID = 8997937682464544625L;

    /**
     * Constructs a new exception with the specified detail message. The cause
     * is not initialized, and may subsequently be initialized by a call to
     * {@link #initCause}.
     * 
     * @param message the detail message. The detail message is saved for later
     *        retrieval by the {@link #getMessage()} method.
     */
    public StateDefException(final String message) {
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
    public StateDefException(final String message, final Exception cause) {
        super(message, cause);
    }
}
