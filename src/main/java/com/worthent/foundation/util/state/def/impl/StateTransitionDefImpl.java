/*
 * Copyright 2000-2011 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state.def.impl;

import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.TransitionActor;
import com.worthent.foundation.util.state.def.StateTransitionDef;

import java.util.Collections;
import java.util.List;

/**
 * Immutably encapsulates the information for a state transition definition.
 * 
 * @author Erik K. Worth
 * @version $Id: StateTransitionDefImpl.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
 */
public class StateTransitionDefImpl<D extends StateTableData, E extends StateEvent> implements StateTransitionDef<D, E> {

    /** The identifier of the event that triggers this state transition */
    private final String onEvent;

    /**
     * The identifier of the state to which the table transitions when
     * successful
     */
    private final String goToState;

    /**
     * The list of {@link TransitionActor} implementations executed during the
     * state transition.
     */
    private final List<TransitionActor<D, E>> actors;

    /**
     * Construct a state transition object. Instances of this object define the
     * event that triggers a state transition to a given state and the actions
     * to perform on the state transition.
     *
     * @param onEvent the name of the event that triggers the transition
     * @param goToState the state this transitions goes to
     * @param actors the actions to take when the transition occurs
     */
    public StateTransitionDefImpl(
            final String onEvent,
            final String goToState,
            final List<TransitionActor<D, E>> actors) {
        if (null == onEvent) {
            throw new IllegalArgumentException("onEvent must not be null");
        }
        if (null == goToState) {
            throw new IllegalArgumentException("goToState must not be null");
        }
        if (null == actors) {
            throw new IllegalArgumentException("actors must not be null");
        }
        this.onEvent = onEvent;
        this.goToState = goToState;
        this.actors = Collections.unmodifiableList(actors);
    }

    /**
     * Construct a state transition object. Instances of this object define the
     * event that triggers a state transition to a given state and the action
     * to perform on the state transition.
     *
     * @param onEvent the name of the event that triggers the transition
     * @param goToState the state this transitions goes to
     * @param actor the action to take when the transition occurs
     */
    public StateTransitionDefImpl(
            final String onEvent,
            final String goToState,
            final TransitionActor<D, E> actor) {
        this(onEvent, goToState, Collections.singletonList(actor));
    }

    /**
     * Construct a state transition object. Instances of this object define the
     * event that triggers a state transition to a given state and the action
     * to perform on the state transition.
     *
     * @param onEvent the name of the event that triggers the transition
     * @param goToState the state this transitions goes to
     */
    public StateTransitionDefImpl(
            final String onEvent,
            final String goToState) {
        this(onEvent, goToState, Collections.emptyList());
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof StateTransitionDef)) {
            return false;
        }

        final StateTransitionDefImpl<?, ?> that = (StateTransitionDefImpl<?, ?>) other;
        return onEvent.equals(that.getEventName()) && goToState.equals(that.getTargetStateName());
    }

    @Override
    public int hashCode() {
        return 31 * onEvent.hashCode() + goToState.hashCode();
    }

    /**
     * Returns the event identifier associated with this transition.
     */
    @Override
    public final String getEventName() {
        return onEvent;
    }

    /**
     * Returns the identifier for the target state when the transition
     * completes.
     */
    @Override
    public final String getTargetStateName() {
        return goToState;
    }

    /**
     * Returns the string identifiers for the actions to be taken on this
     * transition.
     */
    @Override
    public final List<TransitionActor<D, E>> getActors() {
        return actors;
    }
}
