/*
 * Copyright 2000-2011 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state.def;

import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.TransitionActors;
import com.worthent.foundation.util.state.def.impl.StateTransitionDefImpl;

/**
 * A collection of static methods that return re-usable state transition definitions.
 * @author Erik K. Worth
 */
public class StateTransitionDefs {

    /**
     * Returns a default state transition definition that stays in the same state and throws an exception indicating
     * there event not expected.
     * @param <D> the state table data type
     * @param <E> the event type
     */
    public static <D extends StateTableData, E extends StateEvent> StateTransitionDef<D, E> getUnexpectedEventDefaultTransition() {
        return new StateTransitionDefImpl<>(
                StateTransitionDef.DEFAULT_HANDLER_EVENT_ID,
                StateDef.STAY_IN_STATE,
                TransitionActors.unexpectedEventActor());
    }

    /**
     * Returns a default state transition definition that stays in the same state silently ignoring the event.
     * @param <D> the state table data type
     * @param <E> the event type
     */
    public static <D extends StateTableData, E extends StateEvent> StateTransitionDef<D, E> getNoActionDefaultTransition() {
        return new StateTransitionDefImpl<>(
                StateTransitionDef.DEFAULT_HANDLER_EVENT_ID,
                StateDef.STAY_IN_STATE);
    }

    /**
     * Returns a transition for the specified event that transitions to the specified state with no actions.
     *
     * @param onEvent the event triggering the state transition
     * @param toState the target state for the transition
     * @param <D>     the state table data type
     * @param <E>     the event type
     * @return a transition for the specified event that transitions to the specified state with no actions
     */
    public static <D extends StateTableData, E extends StateEvent> StateTransitionDef<D, E> getNoActionTransition(
            final String onEvent,
            final String toState) {
        return new StateTransitionDefImpl<>(onEvent, toState);
    }
}
