/*
 * Copyright 2000-2015 Worth Enterprises, Inc. All Rights Reserved.
 */
package com.worthent.foundation.util.state.data.impl;

import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateExeException;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.data.StateTableDataManager;
import com.worthent.foundation.util.state.def.StateDefException;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A flexible implementation of the state table data manager that uses functions to initialize, retrieve and update the
 * state table data.
 */
public class StateTableDataManagerImpl<D extends StateTableData, E extends StateEvent> implements StateTableDataManager<D, E> {

    /** Provided for state tables that need to initialize the state table data */
    private final Runnable dataInitializer;

    /** Function that returns the state table data from the event */
    private final Function<E, D> dataGetterFunction;

    /** Consumer provided when the updated state table data needs to be set back into a store or the working copy */
    private final BiConsumer<E, D> dataSetterConsumer;

    StateTableDataManagerImpl(
            final Runnable dataInitializer,
            final Function<E, D> dataGetterFunction,
            final BiConsumer<E, D> dataSetterConsumer) {
        if (null == dataGetterFunction) {
            throw new IllegalArgumentException("dataGetterFunction must not be null");
        }
        this.dataInitializer = dataInitializer;
        this.dataGetterFunction = dataGetterFunction;
        this.dataSetterConsumer = dataSetterConsumer;
    }

    @Override
    public void initializeStateTableData() throws StateDefException {
        if (null != dataInitializer) {
            dataInitializer.run();
        }
    }

    @Override
    public D getStateTableData(E event) throws StateExeException {
        return dataGetterFunction.apply(event);
    }

    @Override
    public void setStateTableData(E event, D dataObject) throws StateExeException {
        if (null != dataSetterConsumer) {
            dataSetterConsumer.accept(event, dataObject);
        }
    }
}
