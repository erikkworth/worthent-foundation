/*
 * Copyright 2000-2015 Worth Enterprises, Inc. All Rights Reserved.
 */
package com.worthent.foundation.util.state.data;

import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableBuilder;
import com.worthent.foundation.util.state.StateTableData;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Specifies the operations used to build a state table data manager component.
 * @author Erik K. Worth
 */
public interface StateTableDataManagerBuilder<D extends StateTableData, E extends StateEvent> {

    /** Provided for state tables that need to initialize the state table data */
    StateTableDataManagerBuilder<D, E> withInitializer(Runnable dataInitializer);

    /** Function that returns the state table data from the event */
    StateTableDataManagerBuilder<D, E> withDataGetter(Function<E, D> dataGetterFunction);

    /** Consumer provided when the updated state table data needs to be set back into a store or the working copy */
    StateTableDataManagerBuilder<D, E> withDataSetter(BiConsumer<E, D> dataSetterConsumer);

    /** Returns the state table builder that launched this builder after setting the state table manager */
    StateTableBuilder<D, E> endDataManager();

    /** Returns the state table manager */
    StateTableDataManager<D, E> build();
}
