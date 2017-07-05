/*
 * Copyright 2000-2015 Worth Enterprises, Inc. All Rights Reserved.
 */
package com.worthent.foundation.util.state;

import com.worthent.foundation.util.annotation.NotNull;
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

    /**
     * Set the state table definition into the state table instance being built
     *
     * @param stateTableDef the state table definition
     * @return the instance of this builder
     */
    @NotNull
    StateTableBuilder<D, E> withStateTableDefinition(@NotNull StateTableDef<D, E> stateTableDef);

    /**
     * Returns the builder for the state table definition
     *
     * @return the builder for the state table definition
     */
    @NotNull
    StateTableDefBuilder<D, E> withStateTableDefinition();

    /**
     * Set the state table data manager into the state table instance being built.
     *
     * @param stateTableDataManager the component that retrieves and stores the state table data
     * @return the instance of this builder
     */
    @NotNull
    StateTableBuilder<D, E> withStateTableDataManager(@NotNull StateTableDataManager<D, E> stateTableDataManager);

    /**
     * Starts building a state table data manager and returns its builder
     *
     * @return the builder for the state table data manager
     */
    @NotNull
    StateTableDataManagerBuilder<D, E> withStateTableDataManager();

    /**
     * Set the state table error handler into the state table instance being built
     *
     * @param stateErrorHandler the state table error handler called when there are error during state transitions
     * @return the instance of this builder
     */
    @NotNull
    StateTableBuilder<D, E> withErrorHandler(@NotNull StateErrorHandler<D, E> stateErrorHandler);

    /**
     * Set the state table transitioner into the state table instance being built
     *
     * @param stateTransitioner the state transition component called after every successful state transition
     * @return the instance of this builder
     */
    @NotNull
    StateTableBuilder<D, E> withStateTransitioner(@NotNull StateTransitioner<D, E> stateTransitioner);

    /**
     * Returns an immutable state table instance or throws an exception if it is not properly prepared
     *
     * @return an immutable state table instance
     * @throws StateDefException thrown when there is an error building the state table (typically something missing)
     */
    @NotNull
    StateTable<D, E> build() throws StateDefException;
}
