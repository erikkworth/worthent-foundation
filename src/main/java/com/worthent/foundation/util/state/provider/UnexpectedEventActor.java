/*
 * Copyright 2000-2011 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state.provider;


import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.*;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Reusable actor that serves well as the actor within the default transition
 * (last registered) for a given state. This actor simply throws an exception
 * indicating received event was not expected in the state.
 *
 * @author Erik K. Worth
 * @version $Id: UnexpectedEventActor.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
 */
public final class UnexpectedEventActor<D extends StateTableData, E extends StateEvent> implements TransitionActor<D, E> {

    /**
     * The name of this actor for logging purposes
     */
    private static final String UNEXPECTED_EVENT_ACTOR_NAME =
            "Unexpected Event Actor";

    /**
     * Hide the constructor to enforce the singleton pattern
     */
    public UnexpectedEventActor() {
        // Empty
    }

    /**
     * @return the name of the actor class for logging purposes
     */
    @NotNull
    public String getActorName() {
        return getClass().getName();
    }

    /**
     * Throws an exception indicating the specified event was unexpected in the
     * current state.
     */
    public void onAction(@NotNull final TransitionContext<D, E> context) throws StateExeException {
        checkNotNull(context, "context must not be null");
        final StateTable table = context.getStateTable();
        final StateEvent event = context.getEvent();
        throw new StateExeException("The state table, '" +
                table.getStateTableName() +
                "', received an unexpected event, '" +
                event.getName() +
                "', in state '" +
                context.getFromState() +
                "'.");
    }

    @Override
    public String getName() {
        return UNEXPECTED_EVENT_ACTOR_NAME;
    }
}
