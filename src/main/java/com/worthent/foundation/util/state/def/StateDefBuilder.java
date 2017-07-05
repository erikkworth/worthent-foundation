/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.def;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;

/**
 * Specifies the operation available on the builder for a single state in the state table
 *
 * @author Erik K. Worth
 */
public interface StateDefBuilder<D extends StateTableData, E extends StateEvent> extends StateDef<D, E> {

    /**
     * Start building a state transition for this state on the provided event name
     *
     * @param eventName the identifier for the event that triggers the state transition
     * @return a reference to this builder
     */
    @NotNull
    StateTransitionDefBuilder<D, E> transitionOnEvent(@NotNull String eventName);

    /**
     * Specifies the default state transition handler that handles events not specifically handled by other transitions
     *
     * @return a reference to this builder
     */
    @NotNull
    StateTransitionDefBuilder<D, E> withDefaultEventHandler();

    /**
     * Specifies the default state transition handler that handles events not specifically handled by other transitions
     *
     * @param defaultStateTransition the default state transition handler that handles events not specifically handled
     *                               by other transitions
     * @return a reference to this builder
     */
    @NotNull
    StateDefBuilder<D, E> withDefaultEventHandler(@NotNull StateTransitionDef<D, E> defaultStateTransition);

    /**
     * Appends the provided state transition to the list of transitions
     *
     * @param stateTransition the state transition definition
     * @return a reference to this builder
     */
    @NotNull
    StateDefBuilder<D, E> appendStateTransition(@NotNull StateTransitionDef<D, E> stateTransition);

    /**
     * Builds the state definition, appends it to the state table builder that called this, and returns the state table
     * builder.
     *
     * @return a reference to the parent of this builder
     * @throws StateDefException thrown when a required parameter is missing when building the state definition
     */
    @NotNull
    StateTableDefBuilder<D, E> endState() throws StateDefException;

    /**
     * Returns the completed state definition or throws an exception if a require parameter is missing
     *
     * @return the completed state definition
     * @throws StateDefException thrown when a required parameter is missing
     */
    @NotNull
    StateDef<D, E> build() throws StateDefException;
}
