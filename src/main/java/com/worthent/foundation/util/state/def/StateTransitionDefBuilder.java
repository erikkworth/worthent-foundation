/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state.def;

import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.TransitionActor;

/**
 * Specifies the operations available on the builder for a state transition definition.
 *
 * @author Erik K. Worth
 */
public interface StateTransitionDefBuilder<D extends StateTableData, E extends StateEvent> extends StateTransitionDef<D, E> {

    /** Transition to this state for the provided event (required) */
    StateTransitionDefBuilder<D, E> toState(String targetStateName);

    /** Have this actor perform its function in the order added during the state transition */
    StateTransitionDefBuilder<D, E> withActor(TransitionActor<D, E> actor);

    /** Have the actors with these names perform their function in order during the state transition */
    StateTransitionDefBuilder<D, E> withActorsByName(String... actorNames);

    /** Builds the transition, appends it to the state builder that called this builder, and returns the state builder */
    StateDefBuilder<D, E> endTransition();

    /**
     * Returns a completed state transition definition or throws an exception if one of the arguments is missing.
     */
    StateTransitionDef<D, E> build();
}
