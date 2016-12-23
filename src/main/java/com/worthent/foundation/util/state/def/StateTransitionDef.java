/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state.def;

import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.TransitionActor;

import java.util.List;

/**
 * Specifies the information available on a state transition.
 *
 * @author Erik K. Worth
 * @version $Id: StateTransitionDef.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
 */
public interface StateTransitionDef<D extends StateTableData, E extends StateEvent> {

    /** Event name used to identify the default event handler for any event not explicitly handled */
    String DEFAULT_HANDLER_EVENT_ID = "#Default#";

    /**
     * Returns the event identifier associated with this transition.
     */
    String getEventName();

    /**
     * Returns the identifier for the target state when the transition
     * completes.
     */
    String getTargetStateName();

    /**
     * Returns the string identifiers for the actions to be taken on this
     * transition in the order they are executed for a given event.
     */
    List<TransitionActor<D, E>> getActors();
}
