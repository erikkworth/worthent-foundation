/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.def;

import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;

/**
 * Specifies the operation available on the builder for a single state in the state table
 *
 * @author Erik K. Worth
 */
public interface StateDefBuilder<D extends StateTableData, E extends StateEvent> extends StateDef<D, E> {

    /** Start building a state transition for this state on the provided event name */
    StateTransitionDefBuilder<D, E> transitionOnEvent(String eventName);

    /** Specifies the default state transition handler that handles events not specifically handled by other transitions */
    StateTransitionDefBuilder<D, E> withDefaultEventHandler();

    /** Specifies the default state transition handler that handles events not specifically handled by other transitions */
    StateDefBuilder<D, E> withDefaultEventHandler(StateTransitionDef<D, E> defaultStateTransition);

    /** Appends the provided state transition to the list of transitions */
    StateDefBuilder<D, E> appendStateTransition(StateTransitionDef<D, E> stateTransition);

    /** Builds the state definition, appends it to the state table builder that called this, and returns the state
     * table builder.
     */
    StateTableDefBuilder<D, E> endState();

    /** Returns the completed state definition or throws an exception if a require parameter is missing */
    StateDef<D, E> build();
}
