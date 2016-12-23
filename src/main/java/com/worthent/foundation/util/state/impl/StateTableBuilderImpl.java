/*
 * Copyright 2000-2015 Worth Enterprises, Inc. All Rights Reserved.
 */
package com.worthent.foundation.util.state.impl;

import com.worthent.foundation.util.state.*;
import com.worthent.foundation.util.state.data.StateTableDataManager;
import com.worthent.foundation.util.state.data.StateTableDataManagerBuilder;
import com.worthent.foundation.util.state.data.impl.StateTableDataManagerBuilderImpl;
import com.worthent.foundation.util.state.def.StateDefException;
import com.worthent.foundation.util.state.def.StateTableDef;
import com.worthent.foundation.util.state.def.StateTableDefBuilder;
import com.worthent.foundation.util.state.def.impl.StateTableDefBuilderImpl;

/**
 * Implements the top-level state table builder
 * @author Erik K. Worth
 */
public class StateTableBuilderImpl<D extends StateTableData, E extends StateEvent> implements StateTableBuilder<D, E> {

    /** The state table error handler to use */
    private StateErrorHandler errorHandler;

    /** The state transitioner to use */
    private StateTransitioner<D, E> stateTransitioner;

    /** The state table definition */
    private StateTableDef<D, E> stateTblDef;

    /** Keeps track of current and prior state table state */
    private StateTableDataManager<D, E> stateTableDataManager;

    @Override
    public StateTableBuilder<D, E> withStateTableDefinition(final StateTableDef<D, E> stateTableDef) {
        this.stateTblDef = stateTableDef;
        return this;
    }

    @Override
    public StateTableDefBuilder<D, E> withStateTableDefinition() {
        return new StateTableDefBuilderImpl<>(this);
    }

    @Override
    public StateTableBuilder<D, E> withStateTableDataManager(StateTableDataManager<D, E> stateTableDataManager) {
        this.stateTableDataManager = stateTableDataManager;
        return this;
    }

    @Override
    public StateTableDataManagerBuilder<D, E> withStateTableDataManager() {
        return new StateTableDataManagerBuilderImpl<>(this);
    }

    @Override
    public StateTableBuilder<D, E> withErrorHandler(StateErrorHandler<D, E> stateErrorHandler) {
        this.errorHandler = stateErrorHandler;
        return this;
    }

    @Override
    public StateTableBuilder<D, E> withStateTransitioner(StateTransitioner<D, E> stateTransitioner) {
        this.stateTransitioner = stateTransitioner;
        return this;
    }

    @Override
    public StateTable<D, E> build() throws StateDefException {
        return new StateTableImpl<>(stateTblDef, stateTableDataManager, errorHandler, stateTransitioner);
    }
}
