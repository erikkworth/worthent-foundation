/*
 * Copyright 2000-2011 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state.data;

import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateExeException;
import com.worthent.foundation.util.state.StateTableControl;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.def.StateDefException;

/**
 * Specifies the operations used to manage the data object for a state table.
 * @author Erik K. Worth
 */
public interface StateTableDataManager<D extends StateTableData, E extends StateEvent> {
    /**
     * Sets the current state in the state table data object to the initial
     * state of the state table. This is typically called from the
     * implementation of the {@link StateTableControl#start()} method.
     *
     * @throws StateDefException thrown when there is an error initializing the
     *             state table
     */
    void initializeStateTableData() throws StateDefException;

    /**
     * Returns a copy of the state table data object that minimally holds the
     * current and prior states of the state table instance. This method is
     * called by the engine when the event processing begins for the specified
     * event. This method is often implemented to return a copy of the data that
     * is then modified by the state transition actors. If the processing completes
     * successfully, the engine sets the updated copy back into this state table via
     * a call to {@link #setStateTableData(StateEvent, StateTableData)}.
     *
     * @param event the event being processed
     *
     * @throws StateExeException thrown when there is an error retrieving the
     *             state history
     */
    D getStateTableData(E event) throws StateExeException;

    /**
     * Updates the state table instance with a new value of the data object that
     * minimally holds the current and prior states of the state table instance.
     * This method is called by the engine when the processing has completed
     * successfully for the event. The data object passed in here is the updated
     * copy modified by the state transition actors.
     *
     * @param event the event that triggered the state table to update its data
     * @param dataObject the new data object to set into the state table
     *            instance
     *
     * @throws StateExeException thrown when there is an error setting the state
     *             history
     */
    void setStateTableData(E event, D dataObject) throws StateExeException;
}
