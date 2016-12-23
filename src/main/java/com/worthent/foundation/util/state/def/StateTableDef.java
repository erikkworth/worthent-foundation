/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.def;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateExeException;
import com.worthent.foundation.util.state.StateTableData;

/**
 * Defines a state transition table from the collection of states that can
 * exist.
 * <p>
 * This implementation assumes that you can define a state transition table with
 * a well defined set of states that respond to a set of well defined events. It
 * further assumes that a specific state transition occurs when a specific event
 * arrives while the table is in a given state. This means that there is a one
 * to one correspondence between an event and a transition from a given state.
 * This implementation does not support conditional transitions on a given event
 * (but you can generally define another event and another transition to handle
 * these cases).
 * <p>
 * This implementation further assumes that the work you want to the state table
 * to perform all takes place on state transitions (and not the entry to and
 * exit from a given state).
 * <p>
 * What this all boils down to is a state table that is defined from a set of
 * states, events, and transition actors. States and events are defined by
 * string names and must be unique. Transition actors are defined by names, but
 * also must have a corresponding object associated with each one (actor
 * objects). The actor objects implement a common interface according to the
 * Template Method pattern (<i>Design Patterns: Elements of Reusable
 * Object-Oriented Software</i> by Erich Gamma and others). When a state
 * transition is triggered by an event, the state table dispatches the various
 * actions through the actor objects.
 * <p>
 * This implementation supports two other dispatch type mechanisms on:
 * <ol>
 * <li>State Transitions
 * <li>Error Handlers
 * </ol>
 * You may install a state transitioner object to take action every time the
 * state table successfully changes state. This is handy when you need to log
 * state transitions or update some persistent object's state.
 * <p>
 * You may also install an error handler object to take action when state
 * transitions fail. Your actor objects may throw exceptions if they are unable
 * to perform their designated actions. When an exception is thrown, the state
 * transition is stopped and subsequent actions are aborted. The error handler
 * is invoked so you can log the problem and/or issue a message to the user.
 * 
 * @see StateDef
 * 
 * @author Erik K. Worth
 * @version $Id: StateTableDef.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
 */
public interface StateTableDef<D extends StateTableData, E extends StateEvent> {

    /**
     * Returns the state table's name or <code>null</code> when none is set.
     */
    @Nullable
    String getName();

    /**
     * Returns the initial state. The initial state is the first state added to
     * this state table definition.
     */
    @NotNull
    StateDef<D, E> getInitialState() throws StateDefException;

    /**
     * Returns <code>true</code> if the specified state identifier is present in
     * the state table.
     */
    boolean containsState(@NotNull String stateName);

    /**
     * Returns the state table metadata for the specified state name or
     * <code>null</code> if the state is not found.
     * 
     * @param stateName the name of the state
     * @return the state table metadata for the specified state name or
     *         <code>null</code> if the state is not found
     */
    @Nullable
    StateDef<D, E> getState(@NotNull String stateName);

    /**
     * Returns the transition configured to respond to the specified event when
     * the table is in the specified state.
     * 
     * @param stateName the string identifier for the state
     * @param eventName the string identifier for the event
     * @exception StateExeException thrown when the state does not exist in the
     *            state table
     */
    @NotNull
    StateTransitionDef<D, E> getTransition(
        @NotNull String stateName,
        @NotNull String eventName) throws StateExeException;

}
