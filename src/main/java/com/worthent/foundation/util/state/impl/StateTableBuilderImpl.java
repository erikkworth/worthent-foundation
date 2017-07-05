/*
 * Copyright 2000-2015 Worth Enterprises, Inc. All Rights Reserved.
 */
package com.worthent.foundation.util.state.impl;

import com.worthent.foundation.util.annotation.NotNull;
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
 *
 * @author Erik K. Worth
 */
public class StateTableBuilderImpl<D extends StateTableData, E extends StateEvent> implements StateTableBuilder<D, E> {

    /** The state table error handler to use */
    private StateErrorHandler<D, E> errorHandler;

    /** The state transitioner to use */
    private StateTransitioner<D, E> stateTransitioner;

    /** The state table definition */
    private StateTableDef<D, E> stateTblDef;

    /** Keeps track of current and prior state table state */
    private StateTableDataManager<D, E> stateTableDataManager;

    @Override
    @NotNull
    public StateTableBuilder<D, E> withStateTableDefinition(@NotNull final StateTableDef<D, E> stateTableDef) {
        this.stateTblDef = stateTableDef;
        return this;
    }

    @Override
    @NotNull
    public StateTableDefBuilder<D, E> withStateTableDefinition() {
        return new StateTableDefBuilderImpl<>(this);
    }

    @Override
    @NotNull
    public StateTableBuilder<D, E> withStateTableDataManager(@NotNull StateTableDataManager<D, E> stateTableDataManager) {
        this.stateTableDataManager = stateTableDataManager;
        return this;
    }

    @Override
    @NotNull
    public StateTableDataManagerBuilder<D, E> withStateTableDataManager() {
        return new StateTableDataManagerBuilderImpl<>(this);
    }

    @Override
    @NotNull
    public StateTableBuilder<D, E> withErrorHandler(@NotNull StateErrorHandler<D, E> stateErrorHandler) {
        this.errorHandler = stateErrorHandler;
        return this;
    }

    @Override
    @NotNull
    public StateTableBuilder<D, E> withStateTransitioner(@NotNull StateTransitioner<D, E> stateTransitioner) {
        this.stateTransitioner = stateTransitioner;
        return this;
    }

    @Override
    @NotNull
    public StateTable<D, E> build() throws StateDefException {
        if (null == stateTblDef) {
            throw new StateDefException("Missing state table definition");
        }
        if (null == stateTableDataManager) {
            throw new StateDefException("Missing state table data manager");
        }
        return new StateTableImpl<>(stateTblDef, stateTableDataManager, errorHandler, stateTransitioner);
    }
}
