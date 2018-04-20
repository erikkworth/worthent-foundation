package com.worthent.foundation.util.state.def.impl;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;
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

import static com.worthent.foundation.util.condition.Preconditions.checkNotBlank;
import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

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
            @Nullable final StateTableDefBuilder<D, E> parentBuilder,
            @NotNull final TransitionActorManager<D, E> transitionActorManager,
            @NotNull final String stateName) {
        super(parentBuilder);
        this.transitionActorManager = checkNotNull(transitionActorManager, "transitionActorManager must not be null");
        this.stateName = checkNotBlank(stateName, "stateName must not be blank");
        this.transitions = new LinkedList<>();
    }

    @Override
    @NotNull
    public StateTransitionDefBuilder<D, E> transitionOnEvent(@NotNull final String eventName) {
        checkNotBlank(eventName, "eventName must not be blank");
        return new StateTransitionDefBuilderImpl<>(this, transitionActorManager, eventName);
    }

    @Override
    @NotNull
    public StateTransitionDefBuilder<D, E> transitionOnEvent(@NotNull final Enum<?> event) {
        return transitionOnEvent(checkNotNull(event, "event must not be blank").name());
    }

    @Override
    @NotNull
    public StateTransitionDefBuilder<D, E> withDefaultEventHandler() {
        return new StateTransitionDefBuilderImpl<>(this, transitionActorManager, StateTransitionDefImpl.DEFAULT_HANDLER_EVENT_ID);
    }

    @Override
    @NotNull
    public StateDefBuilder<D, E> withDefaultEventHandler(@NotNull final StateTransitionDef<D, E> defaultStateTransition) {
        checkNotNull(defaultStateTransition, "defaultStateTransition must not be null");
        if (!StateTransitionDefImpl.DEFAULT_HANDLER_EVENT_ID.equals(defaultStateTransition.getEventName())) {
            throw new IllegalArgumentException(
                    "Expected the default state transition to have the special event name, " +
                            StateTransitionDefImpl.DEFAULT_HANDLER_EVENT_ID);
        }
        defaultTransition = defaultStateTransition;
        return this;
    }

    @Override
    @NotNull
    public StateDefBuilder<D, E> appendStateTransition(@NotNull StateTransitionDef<D, E> stateTransition) {
        checkNotNull(stateTransition, "stateTransition must not be null");
        if (StateTransitionDefImpl.DEFAULT_HANDLER_EVENT_ID.equals(stateTransition.getEventName())) {
            defaultTransition = stateTransition;
        } else {
            transitions.add(stateTransition);
        }
        return this;
    }

    @Override
    @NotNull
    public StateTableDefBuilder<D, E> endState() {
        final StateTableDefBuilder<D, E> parentBuilder = getParentBuilder();
        final StateDef<D, E> state = build();
        parentBuilder.appendState(state);
        return parentBuilder;
    }

    @Override
    @NotNull
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
    @Nullable
    public StateTransitionDef<D, E> getTransitionForEvent(@NotNull final String eventName) {
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
