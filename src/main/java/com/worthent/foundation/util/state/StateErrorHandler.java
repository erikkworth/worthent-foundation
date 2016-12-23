/*
 * Copyright 2000-2011 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state;

/**
 * Specifies methods implemented by objects that handler errors (exceptions)
 * that occur during state transitions.
 * 
 * @author Erik K. Worth
 * @version $Id: StateErrorHandler.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
 */
public interface StateErrorHandler<D extends StateTableData, E extends StateEvent> {

    /**
     * This method is called to report errors that occur during state
     * transitions.
     * 
     * Concrete instances of this interface are created and inserted into the
     * table before the table is initialized.
     * 
     * @param context the context for the state transition
     * @param actor the actor that caused the failure (may be <code>null</code>)
     * @param cause the exception that caused the error
     */
    void onError(TransitionContext<D, E> context, TransitionActor<D, E> actor, Exception cause);

}
