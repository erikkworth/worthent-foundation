/**
 * Copyright 2000-2015 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.impl;

import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTable;
import com.worthent.foundation.util.state.StateTableControl;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.TransitionContext;
import com.worthent.foundation.util.state.def.StateTransitionDef;

import java.util.Set;

/**
 * Encapsulates all the elements involved in the state table transition.
 * @author Erik K. Worth
 */
public class TransitionContextImpl<D extends StateTableData, E extends StateEvent> implements TransitionContext<D, E> {

    private final String fromState;
    private final String toState;
    private final StateTable<D, E> stateTable;
    private final D stateTableData;
    private final StateTableControl<E> stateTableControl;
    private final E event;

    public TransitionContextImpl(
            final String fromState,
            final String toState,
            final StateTable<D, E> stateTable,
            final D stateTableData,
            final StateTableControl<E> stateTableControl,
            final E event) {
        this.fromState = fromState;
        this.toState = toState;
        this.stateTable = stateTable;
        this.stateTableData = stateTableData;
        this.stateTableControl = stateTableControl;
        this.event = event;
    }

    @Override
    public String getFromState() {
        return fromState;
    }

    @Override
    public String getToState() {
        return toState;
    }

    @Override
    public StateTable<D, E> getStateTable() {
        return stateTable;
    }

    @Override
    public D getStateTableData() {
        return stateTableData;
    }

    @Override
    public StateTableControl<E> getStateTableControl() {
        return stateTableControl;
    }

    @Override
    public E getEvent() {
        return event;
    }

    public Set<String> getPotentialTargetStates() {
        return stateTable.getStateTableDefinition()
                .getTransition(fromState, event.getName())
                .getPotentialTargetStateNames();
    }
}
