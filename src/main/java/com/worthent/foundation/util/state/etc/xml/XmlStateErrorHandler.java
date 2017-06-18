/*
 * Copyright 2000-2011 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state.etc.xml;

import com.worthent.foundation.util.state.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple implementation of the <code>StateErrorHandler</code> that logs errors using the specified logger.
 * 
 * @author Erik K. Worth
 */
public class XmlStateErrorHandler implements StateErrorHandler<XmlData, XmlEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlStateErrorHandler.class);

    private static final String MSG_TEMPLATE = "There was an error in state table, {}, while in state {} " +
            "transitioning to state {} processing event '{}' near line {} at the XML element path '{}': {}";

    /** Logs the error */
    public void onError(
            final TransitionContext<XmlData, XmlEvent> context,
            final TransitionActor<XmlData, XmlEvent> actor,
            final Exception cause) {
        final XmlData xmlData = context.getStateTableData();
        LOGGER.error(
                MSG_TEMPLATE,
                context.getStateTable().getStateTableName(),
                context.getFromState(),
                context.getToState(),
                context.getEvent().getName(),
                xmlData.getLineNumber(),
                xmlData.getElementPath(),
                cause.getMessage());
    }
}
