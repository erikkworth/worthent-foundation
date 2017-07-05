/*
 * Copyright 2000-2015 Worth Enterprises, Inc. All Rights Reserved.
 */
package com.worthent.foundation.util.state.data.impl;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableBuilder;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.data.StateTableDataManager;
import com.worthent.foundation.util.state.data.StateTableDataManagerBuilder;
import com.worthent.foundation.util.state.def.StateDefException;
import com.worthent.foundation.util.state.def.impl.AbstractChildBuilder;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Implements the builder for the state table data manager.
 * @author Erik K. Worth
 */
public class StateTableDataManagerBuilderImpl<D extends StateTableData, E extends StateEvent> extends AbstractChildBuilder<StateTableBuilder<D, E>> implements StateTableDataManagerBuilder<D, E> {

    /** Provided for state tables that need to initialize the state table data */
    private Runnable dataInitializer;

    /** Function that returns the state table data from the event */
    private Function<E, D> dataGetterFunction;

    /** Consumer provided when the updated state table data needs to be set back into a store or the working copy */
    private BiConsumer<E, D> dataSetterConsumer;

    public StateTableDataManagerBuilderImpl() {
        // empty
    }

    public StateTableDataManagerBuilderImpl(final StateTableBuilder<D, E> parentBuilder) {
        super(parentBuilder);
    }

    @Override
    public StateTableDataManagerBuilder<D, E> withInitializer(@NotNull Runnable dataInitializer) {
        this.dataInitializer = dataInitializer;
        return this;
    }

    @Override
    public StateTableDataManagerBuilder<D, E> withDataGetter(@NotNull Function<E, D> dataGetterFunction) {
        this.dataGetterFunction = dataGetterFunction;
        return this;
    }

    @Override
    public StateTableDataManagerBuilder<D, E> withDataSetter(@NotNull BiConsumer<E, D> dataSetterConsumer) {
        this.dataSetterConsumer = dataSetterConsumer;
        return this;
    }

    @Override
    public StateTableBuilder<D, E> endDataManager() throws StateDefException {
        final StateTableBuilder<D, E> parentBuilder = getParentBuilder();
        final StateTableDataManager<D, E> stateTableDataManager = build();
        parentBuilder.withStateTableDataManager(stateTableDataManager);
        return parentBuilder;
    }

    @Override
    public StateTableDataManager<D, E> build() throws StateDefException {
        if (null == dataGetterFunction) {
            throw new StateDefException("Missing the Data Getter Function");
        }
        return new StateTableDataManagerImpl<D, E>(dataInitializer, dataGetterFunction, dataSetterConsumer);
    }
}
