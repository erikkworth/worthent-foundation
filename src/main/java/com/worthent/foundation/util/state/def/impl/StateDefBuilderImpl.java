/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.def.impl;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.def.StateDefBuilder;
import com.worthent.foundation.util.state.def.StateDef;
import com.worthent.foundation.util.state.def.StateTableDefBuilder;
import com.worthent.foundation.util.state.def.StateTransitionDefBuilder;
import com.worthent.foundation.util.state.def.StateTransitionDef;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Implements the builder that defines a state in a state table.
 *
 * @author Erik K. Worth
 */
public class StateDefBuilderImpl<D extends StateTableData, E extends StateEvent>
        extends AbstractChildBuilder<StateTableDefBuilder<D, E>> implements StateDefBuilder<D, E> {

    /** The transition actor manager holding transition actors discovered from class scans for annotations */
    private final TransitionActorManager<D, E> transitionActorManager;

    /** The name of the name of the current state */
    private final String stateName;

    /** The list of state transitions from this state */
    private final List<StateTransitionDef<D, E>> transitions;

    /** The default event handler for events not explicitly handled in other transitions */
    private StateTransitionDef<D, E> defaultTransition;

    /**
     * Construct from elements.
     *
     * @param parentBuilder the state builder that created this builder
     * @param transitionActorManager the transition manager holding transition actors found during class scans
     * @param stateName the name of this state in the state table
     */
    StateDefBuilderImpl(
            final StateTableDefBuilder<D, E> parentBuilder,
            final TransitionActorManager<D, E> transitionActorManager,
            final String stateName) {
        super(parentBuilder);
        if (null == transitionActorManager) {
            throw new IllegalArgumentException("transitionActorManager must not be null");
        }
        if (null == stateName) {
            throw new IllegalArgumentException("stateName must not be null");
        }
        if (stateName.trim().length() == 0) {
            throw new IllegalArgumentException("stateName must not be blank");
        }
        this.transitionActorManager = transitionActorManager;
        this.stateName = stateName;
        this.transitions = new LinkedList<>();
    }

    @Override
    public StateTransitionDefBuilder<D, E> transitionOnEvent(final String eventName) {
        return new StateTransitionDefBuilderImpl<>(this, transitionActorManager, eventName);
    }

    @Override
    public StateTransitionDefBuilder<D, E> withDefaultEventHandler() {
        return new StateTransitionDefBuilderImpl<>(this, transitionActorManager, StateTransitionDefImpl.DEFAULT_HANDLER_EVENT_ID);
    }

    @Override
    public StateDefBuilder<D, E> withDefaultEventHandler(final StateTransitionDef<D, E> defaultStateTransition) {
        if (null == defaultStateTransition) {
            throw new IllegalArgumentException("stateTransition must not be null");
        }
        if (!StateTransitionDefImpl.DEFAULT_HANDLER_EVENT_ID.equals(defaultStateTransition.getEventName())) {
            throw new IllegalArgumentException(
                    "Expected the default state transition to have the special event name, " +
                            StateTransitionDefImpl.DEFAULT_HANDLER_EVENT_ID);
        }
        defaultTransition = defaultStateTransition;
        return this;
    }

    @Override
    public StateDefBuilder<D, E> appendStateTransition(StateTransitionDef<D, E> stateTransition) {
        if (null == stateTransition) {
            throw new IllegalArgumentException("stateTransition must not be null");
        }
        if (StateTransitionDefImpl.DEFAULT_HANDLER_EVENT_ID.equals(stateTransition.getEventName())) {
            defaultTransition = stateTransition;
        } else {
            transitions.add(stateTransition);
        }
        return this;
    }

    @Override
    public StateTableDefBuilder<D, E> endState() {
        final StateTableDefBuilder<D, E> parentBuilder = getParentBuilder();
        final StateDef<D, E> state = build();
        parentBuilder.appendState(state);
        return parentBuilder;
    }

    @Override
    public StateDef<D, E> build() {
        return (defaultTransition == null)
                ? new StateDefImpl<>(stateName, transitions)
                : new StateDefImpl<>(stateName, transitions, defaultTransition);
    }

    @NotNull
    @Override
    public String getName() {
        return stateName;
    }

    @NotNull
    @Override
    public Collection<StateTransitionDef<D, E>> getTransitions() {
        return transitions;
    }

    @Override
    public StateTransitionDef<D, E> getTransitionForEvent(final String eventName) {
        for (StateTransitionDef<D, E> transition : transitions) {
            if (transition.getEventName().equals(eventName)) {
                return transition;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public StateTransitionDef<D, E> getDefaultTransition() {
        return defaultTransition;
    }
}
