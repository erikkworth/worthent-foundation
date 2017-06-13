/*
 * Copyright 2000-2011 Worth Enterprises, Inc. All rights reserved.
 */
package com.worthent.foundation.util.state;

import com.worthent.foundation.util.state.data.StateTableDataManager;
import com.worthent.foundation.util.state.def.StateTableDef;

/**
 * Specifies the methods implemented by objects that represent an instance of
 * the state table. Implementations must be able to acquire the state table
 * metadata ({@link StateTableDef}) and acquire the current and prior state of
 * the state table and update the new current and prior states based on
 * information in the {@link StateEvent}. Implementations may retrieve and store
 * state information using some persistent store such as a database or keep the
 * information in memory depending on the life span of the state table.
 * Implementations may also provide additional functionality to retrieve and
 * update the object or objects acted upon by the state table.
 * 
 * @author Erik K. Worth
 * @version $Id: StateTable.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
 */
public interface StateTable<D extends StateTableData, E extends StateEvent> {
    /** Returns the name of the state table */
    String getStateTableName();

    /**
     * Returns the object that responds to each state transition or
     * <code>null</code> if none was registered.
     */
    StateTransitioner<D, E> getTransitioner();

    /**
     * Returns the registered handler that responds to state transition errors
     * or <code>null</code> if none is registered.
     */
    StateErrorHandler<D, E> getErrorHandler();

    /** Returns the state table metadata */
    StateTableDef<D, E> getStateTableDefinition(E event) throws StateExeException;

    /** Returns the manager for state table data */
    StateTableDataManager<D, E> getStateTableDataManager();
}
