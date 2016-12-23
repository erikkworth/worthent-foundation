/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.def.impl;

import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.TransitionActor;
import com.worthent.foundation.util.state.def.StateDefBuilder;
import com.worthent.foundation.util.state.def.StateTransitionDefBuilder;
import com.worthent.foundation.util.state.def.StateTransitionDef;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements the builder for state transitions.
 *
 * @author Erik K. Worth
 */
public class StateTransitionDefBuilderImpl<D extends StateTableData, E extends StateEvent>
        extends AbstractChildBuilder<StateDefBuilder<D, E>> implements StateTransitionDefBuilder<D, E> {

    /** The transition actor manager holding transition actors discovered from class scans for annotations */
    private final TransitionActorManager<D, E> transitionActorManager;

    /** The name of the event that triggers this state transition from the current state */
    private final String eventName;

    /** The ordered list of transition actors that do work during the state transition */
    private final List<TransitionActor<D, E>> transitionActors;

    /** The name of the state after the state transition should all the actors succeed */
    private String goToState;

    /**
     * Construct from elements.
     *
     * @param parentBuilder the state builder that created this builder
     * @param transitionActorManager the transition manager holding transition actors found during class scans
     * @param onEventName the event name for the event that triggers the state transition building built
     */
    StateTransitionDefBuilderImpl(
            final StateDefBuilder<D, E> parentBuilder,
            final TransitionActorManager<D, E> transitionActorManager,
            final String onEventName) {
        super(parentBuilder);
        if (null == transitionActorManager) {
            throw new IllegalArgumentException("transitionActorManager must not be null");
        }
        if (null == onEventName) {
            throw new IllegalArgumentException("onEventName must not be null");
        }
        if (onEventName.trim().length() == 0) {
            throw new IllegalArgumentException("onEventName must not be blank");
        }
        this.transitionActorManager = transitionActorManager;
        this.eventName = onEventName;
        this.transitionActors = new LinkedList<>();
    }

    @Override
    public String getEventName() {
        return eventName;
    }

    @Override
    public String getTargetStateName() {
        return goToState;
    }

    @Override
    public List<TransitionActor<D, E>> getActors() {
        return transitionActors;
    }

    @Override
    public StateTransitionDefBuilder<D, E> toState(final String goToState) {
        if (null == goToState) {
            throw new IllegalArgumentException("goToState must not be null");
        }
        if (goToState.trim().length() == 0) {
            throw new IllegalArgumentException("goToState must not be blank");
        }
        this.goToState = goToState;
        return this;
    }

    @Override
    public final StateTransitionDefBuilder<D, E> withActor(final TransitionActor<D, E> actor) {
        if (null == actor) {
            throw new IllegalArgumentException("actors must not be null");
        }
        this.transitionActors.add(actor);
        return this;
    }

    @Override
    public StateTransitionDefBuilder<D, E> withActorsByName(String... actorNames) {
        if (null == actorNames) {
            throw new IllegalArgumentException("actorNames must not be null");
        }
        this.transitionActors.addAll(
                Arrays.stream(actorNames)
                        .map(transitionActorManager::getTransitionActor)
                        .collect(Collectors.toList()));
        return this;
    }

    @Override
    public StateDefBuilder<D, E> endTransition() {
        final StateDefBuilder<D, E> parentBuilder = getParentBuilder();
        final StateTransitionDef<D, E> stateTransition = build();
        parentBuilder.appendStateTransition(stateTransition);
        return parentBuilder;
    }

    @Override
    public StateTransitionDef<D, E> build() {
        if (null == goToState) {
            throw new IllegalStateException("no toState provided by transition builder");
        }
        return new StateTransitionDefImpl<>(eventName, goToState, transitionActors);
    }

}
