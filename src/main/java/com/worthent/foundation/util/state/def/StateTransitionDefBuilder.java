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

    /**
     * Transition to this state for the provided event
     *
     * @param targetStateName the target state of the state transition
     * @return a reference to this builder
     */
    @NotNull
    StateTransitionDefBuilder<D, E> toState(@NotNull String targetStateName);

    /**
     * Transition to this state for the provided event
     *
     * @param targetState the target state of the state transition
     * @return a reference to this builder
     */
    @NotNull
    StateTransitionDefBuilder<D, E> toState(@NotNull Enum<?> targetState);

    /**
     * Transition to the state when the corresponding condition is satisfied (after processing the event)
     *
     * @param goToState the target state based on the first condition built by the returned builder
     * @return a reference to transition condition builder
     */
    @NotNull
    ToStateConditionBuilder<D, E> toStateConditionally(@NotNull String goToState);

    /**
     * Transition to the state when the corresponding condition is satisfied (after processing the event)
     *
     * @param goToState the target state based on the first condition built by the returned builder
     * @return a reference to transition condition builder
     */
    @NotNull
    ToStateConditionBuilder<D, E> toStateConditionally(@NotNull Enum<?> goToState);

    /**
     * Transition to the state when the corresponding condition is satisfied (before processing the event)
     *
     * @param goToState the target state based on the first condition built by the returned builder
     * @return a reference to the transition condition builder
     */
    @NotNull
    ToStateConditionBuilder<D, E> toStateConditionallyBeforeEvent(@NotNull String goToState);

    /**
     * Transition to the state when the corresponding condition is satisfied (before processing the event)
     *
     * @param goToState the target state based on the first condition built by the returned builder
     * @return a reference to the transition condition builder
     */
    @NotNull
    ToStateConditionBuilder<D, E> toStateConditionallyBeforeEvent(@NotNull Enum<?> goToState);

    /**
     * Have this actor perform its function in the order added during the state transition
     *
     * @param actor the transition actor to invoke when the state table makes the transition being built
     * @return a reference to this builder
     */
    @NotNull
    StateTransitionDefBuilder<D, E> withActor(@NotNull TransitionActor<D, E> actor);

    /**
     * Have the actors with these names perform their function in order during the state transition
     *
     * @param actorNames the names of actors that have been registered with the state table by name
     * @return a reference to this builder
     */
    @NotNull
    StateTransitionDefBuilder<D, E> withActorsByName(@NotNull String... actorNames);

    /**
     * Builds the transition, appends it to the state builder that called this builder, and returns the state builder
     *
     * @return a reference to parent of this builder
     */
    @NotNull
    StateDefBuilder<D, E> endTransition();

    /**
     * @return a completed state transition definition or throws an exception if one of the arguments is missing.
     */
    @NotNull
    StateTransitionDef<D, E> build();
}
