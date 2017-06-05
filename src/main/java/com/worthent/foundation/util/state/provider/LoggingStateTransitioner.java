/*
 * Copyright 2000-2011 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state.provider;

import com.worthent.foundation.util.state.*;
import com.worthent.foundation.util.state.def.StateDef;
import org.slf4j.Logger;

/**
 * A simple implementation of the <code>StateTransitioner</code> that simply
 * logs transitions using the specified logger and severity level.
 * 
 * @author Erik K. Worth
 * @version $Id: LoggingStateTransitioner.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
 */
public class LoggingStateTransitioner<D extends StateTableData, E extends StateEvent> implements StateTransitioner<D, E> {

    /** The default state transitioner name */
    private static final String TRANSITIONER_NAME =
        "Logging State Transitioner";

    /** The message to log when transitioning from one state to another */
    private static final String TRANSITION_MESSAGE =
        "The state table, {}, transitioned from state, {}, to state, {}, "
            + "having successfully processed the event, {}";

    /** The logger to use to log state transitions */
    private final Logger logger;

    /**
     * Construct with the log category.
     * 
     * @param logger the log category to use when logging transitions
     */
    public LoggingStateTransitioner(final Logger logger) {
        this.logger = logger;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.worthent.foundation.service.spi.util.state.StateTransitioner#getName()
     */
    @Override
    public String getName() {
        return TRANSITIONER_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.worthent.foundation.service.spi.util.state.StateTransitioner#onTransition
     * (java.lang.String, java.lang.String,
     * com.worthent.foundation.service.spi.util.state.StateTableInstance,
     * com.worthent.foundation.service.spi.util.state.StateEvent)
     */
    @Override
    public void onTransition(final TransitionContext<D, E> context) throws StateExeException {
        final String toState = StateDef.STATE_CHANGE_BY_ACTOR.equals(context.getToState())
                ? context.getStateTableData().getCurrentState()
                : context.getToState();
        logger.debug(TRANSITION_MESSAGE,
                context.getStateTable().getStateTableName(),
                context.getFromState(),
                toState,
                context.getEvent().toString());
    }
}
