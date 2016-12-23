/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state;

import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTable;

/**
 * Specifies the information available from the context of transitioning from one state to another while processing
 * an event.
 */
public interface TransitionContext<D extends StateTableData, E extends StateEvent> {

    /** Returns the current state where the event was received */
    String getFromState();

    /** Returns the state to which the table will transition should everything work out */
    String getToState();

    /** Returns a reference to the state table */
    StateTable<D, E> getStateTable();

    /** Returns a working copy of the state table data */
    D getStateTableData();

    /** Returns the state table controller */
    StateTableControl<E> getStateTableControl();

    /** Returns the event that trigger the state transition */
    E getEvent();
}
