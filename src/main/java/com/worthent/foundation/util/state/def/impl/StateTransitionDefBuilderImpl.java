/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.def.impl;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.TransitionActor;
import com.worthent.foundation.util.state.def.*;
import com.worthent.foundation.util.state.provider.ToStateNavigationActor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.worthent.foundation.util.condition.Preconditions.checkNotBlank;
import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

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
    private final LinkedList<TransitionActor<D, E>> transitionActors;

    /** The name of the state after the state transition should all the actors succeed */
    private String goToState;

    /** The list of conditional directives that drive the state table to a state based on state table data or the event */
    private List<ToStateCondition<D, E>> toStateConditions;

    /** Determines whether to check the condition of the state table before or after the actors run */
    private ToStateConditionListBuilderImpl.CheckPosition checkPosition;

    /**
     * Construct from elements.
     *
     * @param parentBuilder the state builder that created this builder
     * @param transitionActorManager the transition manager holding transition actors found during class scans
     * @param onEventName the event name for the event that triggers the state transition building built
     */
    StateTransitionDefBuilderImpl(
            @NotNull final StateDefBuilder<D, E> parentBuilder,
            @NotNull final TransitionActorManager<D, E> transitionActorManager,
            @NotNull final String onEventName) {
        super(parentBuilder);
        this.transitionActorManager = checkNotNull(transitionActorManager, "transitionActorManager must not be null");
        this.eventName = checkNotBlank(onEventName, "onEventName must not be blank");
        this.transitionActors = new LinkedList<>();
        this.toStateConditions = new LinkedList<>();
    }

    /** Called by the ToStateConditionListBuilder to append these conditions */
    void setToStateConditions(
            @NotNull final List<ToStateCondition<D, E>> toStateConditions,
            @NotNull final ToStateConditionListBuilderImpl.CheckPosition checkPosition) {
        this.toStateConditions = (checkNotNull(toStateConditions, "toStateConditions must not be null"));
        this.checkPosition = checkNotNull(checkPosition, "checkPosition must not be null");
    }

    @Override
    @NotNull
    public String getEventName() {
        return eventName;
    }

    @Override
    @NotNull
    public String getTargetStateName() {
        return goToState;
    }

    @Override
    @NotNull
    public List<TransitionActor<D, E>> getActors() {
        return transitionActors;
    }

    @NotNull
    @Override
    public StateTransitionDefBuilder<D, E> toState(@NotNull final String goToState) {
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
    @NotNull
    public ToStateConditionBuilder<D, E> toStateConditionally(@NotNull final String goToState) {
        final ToStateConditionListBuilderImpl<D, E> toStateConditionListBuilder =
                new ToStateConditionListBuilderImpl<>(this, ToStateConditionListBuilderImpl.CheckPosition.AFTER_ACTORS);
        return toStateConditionListBuilder.orToState(checkNotNull(goToState, "goToState must not be null"));
    }

    @Override
    @NotNull
    public ToStateConditionBuilder<D, E> toStateConditionallyBeforeEvent(@NotNull final String goToState) {
        final ToStateConditionListBuilderImpl<D, E> toStateConditionListBuilder =
                new ToStateConditionListBuilderImpl<>(this, ToStateConditionListBuilderImpl.CheckPosition.BEFORE_ACTORS);
        return toStateConditionListBuilder.orToState(checkNotNull(goToState, "goToState must not be null"));
    }

    @Override
    @NotNull
    public final StateTransitionDefBuilder<D, E> withActor(@NotNull final TransitionActor<D, E> actor) {
        if (null == actor) {
            throw new IllegalArgumentException("actors must not be null");
        }
        this.transitionActors.add(actor);
        return this;
    }

    @Override
    @NotNull
    public StateTransitionDefBuilder<D, E> withActorsByName(@NotNull final String... actorNames) {
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
    @NotNull
    public StateDefBuilder<D, E> endTransition() {
        final StateDefBuilder<D, E> parentBuilder = getParentBuilder();
        final StateTransitionDef<D, E> stateTransition = build();
        parentBuilder.appendStateTransition(stateTransition);
        return parentBuilder;
    }

    @Override
    @NotNull
    public StateTransitionDef<D, E> build() {
        if (!toStateConditions.isEmpty()) {
            goToState = StateDef.STATE_CHANGE_BY_ACTOR;
            if (ToStateConditionListBuilderImpl.CheckPosition.AFTER_ACTORS.equals(checkPosition)) {
                transitionActors.add(new ToStateNavigationActor<>(toStateConditions));
            } else {
                transitionActors.addFirst(new ToStateNavigationActor<>(toStateConditions));
            }
        }
        if (null == goToState) {
            throw new IllegalStateException("no toState provided by transition builder");
        }
        return new StateTransitionDefImpl<>(eventName, goToState, transitionActors);
    }

}
