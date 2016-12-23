/*
 * Copyright 2000-2011 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.def.impl;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;
import com.worthent.foundation.util.condition.Preconditions;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.def.StateDef;
import com.worthent.foundation.util.state.def.StateDefException;
import com.worthent.foundation.util.state.def.StateTransitionDef;
import com.worthent.foundation.util.state.def.StateTransitionDefs;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * This class defines a state in a state transition table.
 * 
 * Each state definition has the string name of the state and a list of
 * transitions out of the state. Each transition is identified by an event (i.e.
 * there is one transition defined for each event expected at the state). The
 * transition indicates which state to transition to after all the actions are
 * carried out. The transition also specifies a list of actions to carry out on
 * this state transition.
 * <p>
 * The state table performs work through the state table actions. There is an
 * StateActor registered for each action string defined in the table (the
 * initialize method checks for this). When the state table sees an event, it
 * finds the current state, looks for the transition corresponding to the event
 * and runs each actor corresponding to the actions in the state transition
 * action list. When all of the actions are run, the state table runs the
 * registered transitioner (if one was installed). If an error is detected (the
 * StateException or a runtime exception is thrown), the state transition is
 * aborted and the error handler is run (if installed) with a message indicating
 * the state of the table, events, action, and the message portion of the thrown
 * exception.
 * <p>
 * If no transition is specified for a given event, the default transition for the
 * given state is run.
 * 
 * @author Erik K. Worth
 * @version $Id: StateDef.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
 */
public class StateDefImpl<D extends StateTableData, E extends StateEvent> implements StateDef<D, E> {

    /** The identifier for this state */
    private final String name;

    /**
     * The list of {@link StateTransitionDef} objects providing instructions
     * from transitioning from this state to other states
     */
    private final Map<String, StateTransitionDef<D, E>> transitions;

    /** The default transition is the last one defined in the list */
    private final StateTransitionDef<D, E> defaultTransition;

    /**
     * Defines a state in the state transition table along with a number of
     * state transitions where the default transition throws an exception for an
     * unexpected event.
     *
     * @param name the name of the state
     * @param transitions the transition definitions from this state to other
     *        states
     * @throws StateDefException thrown when there is an error in the state
     *         definition
     */
    StateDefImpl(
            @NotNull final String name,
            @NotNull final List<StateTransitionDef<D, E>> transitions) throws StateDefException {
        this(name, transitions, StateTransitionDefs.getUnexpectedEventDefaultTransition());
    }

    /**
     * Defines a state in the state transition table. Instances of this object
     * define a state and the transitions out of the state.
     *
     * @param stateName the name of the state
     * @param transitions the list of transitions from this state to other
     *        states or to the same state
     * @param defaultTransition transition taken when the received event does
     *        not match one specified on any of the transitions
     */
    StateDefImpl(
            @NotNull final String stateName,
            @NotNull final List<StateTransitionDef<D, E>> transitions,
            @NotNull final StateTransitionDef<D, E> defaultTransition) {
        if (checkNotNull(transitions, "transitions must not be null").isEmpty()) {
            throw new IllegalArgumentException("The transitions list must not be empty");
        }
        this.name = Preconditions.checkNotBlank(stateName, "stateName must not be blank");
        this.transitions = Collections.unmodifiableMap(transitions.stream().collect(
                Collectors.toMap(
                        StateTransitionDef::getEventName,
                        Function.identity(),
                        (u, v) -> {throw new IllegalStateException(String.format("Duplicate transition event: %s", u));},
                        LinkedHashMap::new)));
        this.defaultTransition = checkNotNull(defaultTransition, "defaultTransition must not be null");
    }

    /**
     * Returns the identifier for this state.
     */
    @NotNull
    @Override
    public final String getName() {
        return name;
    }

    /**
     * Returns the <code>StateTransitionDef</code> that originate from this
     * state.
     */
    @NotNull
    @Override
    public final Collection<StateTransitionDef<D, E>> getTransitions() {
        return transitions.values();
    }

    /**
     * Returns the <code>StateTransitionDef</code> that handles the transition
     * for the specified event name or <code>null</code> if no transition is
     * configured for the specified event name.
     */
    @Nullable
    @Override
    public final StateTransitionDef<D, E> getTransitionForEvent(final String eventName) {
        return transitions.get(eventName);
    }

    /**
     * Returns the last state transition defined as the default or
     * <code>null</code> if no transitions have been defined.
     */
    @NotNull
    @Override
    public StateTransitionDef<D, E> getDefaultTransition() {
        return defaultTransition;
    }

}
