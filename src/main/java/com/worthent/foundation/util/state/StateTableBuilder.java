/*
 * Copyright 2000-2015 Worth Enterprises, Inc. All Rights Reserved.
 */
package com.worthent.foundation.util.state;

import com.worthent.foundation.util.state.data.StateTableDataManager;
import com.worthent.foundation.util.state.data.StateTableDataManagerBuilder;
import com.worthent.foundation.util.state.def.StateDefException;
import com.worthent.foundation.util.state.def.StateTableDef;
import com.worthent.foundation.util.state.def.StateTableDefBuilder;

/**
 * Specifies all the operations available to build an instance of a state table.
 * @author Erik K. Worth
 */
public interface StateTableBuilder<D extends StateTableData, E extends StateEvent> {

    /** Set the state table definition into the state table instance being built */
    StateTableBuilder<D, E> withStateTableDefinition(StateTableDef<D, E> stateTableDef);

    /** Returns the builder for the state table definition */
    StateTableDefBuilder<D, E> withStateTableDefinition();

    /** Set the state table data manager into the state table instance being built */
    StateTableBuilder<D, E> withStateTableDataManager(StateTableDataManager<D, E> stateTableDataManager);

    /** Starts building a state table data manager and returns its builder */
    StateTableDataManagerBuilder<D, E> withStateTableDataManager();

    /** Set the state table error handler into the state table instance being built */
    StateTableBuilder<D, E> withErrorHandler(StateErrorHandler<D, E> stateErrorHandler);

    /** Set the state table transitioner into the state table instance being built */
    StateTableBuilder<D, E> withStateTransitioner(StateTransitioner<D, E> stateTransitioner);

    /** Returns a valid state table instance or throws an exception if it is not properly prepared */
    StateTable<D, E> build() throws StateDefException;
}
