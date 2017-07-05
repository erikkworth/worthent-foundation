/*
 * Copyright 2000-2011 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state;

import com.worthent.foundation.util.annotation.NotNull;

/**
 * Specifies methods implemented by objects that need to do something when a
 * state table transitions from one state to another.
 * 
 * @author Erik K. Worth
 * @version $Id: StateTransitioner.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
 */
public interface StateTransitioner<D extends StateTableData, E extends StateEvent> {

    /** The default name of the actor when no name is provided */
    String UNNAMED = "UNNAMED_TRANSITIONER";

    /**
     * Returns the name of the State Transitioner for logging purposes.
     *
     * @return the name of the State Transitioner for logging purposes
     */
    @NotNull
    default String getName() {return UNNAMED;}

    /**
     * This method is called once by the state table when a state transition
     * completes.
     * 
     * @param context the transition context
     *
     * @exception StateExeException thrown when the onTransition method fails
     *            (it will prevent the state transition).
     */
    void onTransition(@NotNull TransitionContext<D, E> context) throws StateExeException;

}
