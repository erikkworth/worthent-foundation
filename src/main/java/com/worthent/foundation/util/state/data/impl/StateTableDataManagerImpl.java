/*
 * Copyright 2000-2015 Worth Enterprises, Inc. All Rights Reserved.
 */
package com.worthent.foundation.util.state.data.impl;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateExeException;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.data.StateTableDataManager;
import com.worthent.foundation.util.state.def.StateDefException;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

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

    /**
     * Construct from data management functions.
     *
     * @param dataInitializer the optional data initializer that is called once when the state table is started
     * @param dataGetterFunction the required data accessor function that returns a reference to the state table data
     * @param dataSetterConsumer an optional data setter that updates the state of the data object from the results of
     *                           data transformation during the state transition
     */
    StateTableDataManagerImpl(
            @Nullable final Runnable dataInitializer,
            @NotNull final Function<E, D> dataGetterFunction,
            @Nullable final BiConsumer<E, D> dataSetterConsumer) {
        this.dataInitializer = dataInitializer;
        this.dataGetterFunction = checkNotNull(dataGetterFunction, "dataGetterFunction must not be null");
        this.dataSetterConsumer = dataSetterConsumer;
    }

    @Override
    public void initializeStateTableData() throws StateDefException {
        if (null != dataInitializer) {
            dataInitializer.run();
        }
    }

    @Override
    public D getStateTableData(@NotNull E event) throws StateExeException {
        checkNotNull(event, "event must not be null");
        return dataGetterFunction.apply(event);
    }

    @Override
    public void setStateTableData(@NotNull E event, @NotNull D dataObject) throws StateExeException {
        if (null != dataSetterConsumer) {
            checkNotNull(event, "event must not be null");
            checkNotNull(dataObject, "dataObject must not be null");
            dataSetterConsumer.accept(event, dataObject);
        }
    }
}
