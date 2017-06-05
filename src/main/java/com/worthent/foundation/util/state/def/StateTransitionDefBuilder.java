/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state.def;

import com.worthent.foundation.util.annotation.NotNull;
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
    @NotNull
    StateTransitionDefBuilder<D, E> toState(@NotNull String targetStateName);

    /** Transition to the state when the corresponding condition is satisfied (after processing the event) */
    @NotNull
    ToStateConditionBuilder<D, E> toStateConditionally(@NotNull String goToState);

    /** Transition to the state when the corresponding condition is satisfied (before processing the event) */
    ToStateConditionBuilder<D, E> toStateConditionallyBeforeEvent(String goToState);

    /** Have this actor perform its function in the order added during the state transition */
    @NotNull
    StateTransitionDefBuilder<D, E> withActor(@NotNull TransitionActor<D, E> actor);

    /** Have the actors with these names perform their function in order during the state transition */
    @NotNull
    StateTransitionDefBuilder<D, E> withActorsByName(@NotNull String... actorNames);

    /** Builds the transition, appends it to the state builder that called this builder, and returns the state builder */
    @NotNull
    StateDefBuilder<D, E> endTransition();

    /**
     * Returns a completed state transition definition or throws an exception if one of the arguments is missing.
     */
    @NotNull
    StateTransitionDef<D, E> build();
}
