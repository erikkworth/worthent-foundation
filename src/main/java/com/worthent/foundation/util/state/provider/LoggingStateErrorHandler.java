/*
 * Copyright 2000-2011 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state.provider;

import com.worthent.foundation.util.state.*;
import org.slf4j.Logger;

/**
 * A simple implementation of the <code>StateErrorHandler</code> that simply
 * logs errors using the specified logger and severity level.
 * 
 * @author Erik K. Worth
 * @version $Id: LoggingStateErrorHandler.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
 */
public class LoggingStateErrorHandler<D extends StateTableData, E extends StateEvent> implements StateErrorHandler<D, E> {

    private static final String MSG_TEMPLATE = "There was an error in state table, {}, while in state, {}, " +
            "transitioning to state, {}, processing event, {}, from the transition actor, {}: {}";

    /** The logger to use to log errors */
    private final Logger logger;

    public LoggingStateErrorHandler(final Logger logger) {
        this.logger = logger;
    }

    /** Logs the error */
    public void onError(final TransitionContext<D, E> context, final TransitionActor<D, E> actor, final Exception cause) {

        final String actorName = (null == actor) ? "N/A" : actor.getName();
        logger.error(
                MSG_TEMPLATE,
                context.getStateTable().getStateTableName(),
                context.getFromState(),
                context.getToState(),
                context.getEvent().getName(),
                actorName,
                cause.getMessage());
    }
}
