package com.worthent.foundation.util.state.data;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableBuilder;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.def.StateDefException;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Specifies the operations used to build a state table data manager component.
 * @author Erik K. Worth
 */
public interface StateTableDataManagerBuilder<D extends StateTableData, E extends StateEvent> {

    /**
     * The optional component used to initialize the state table data
     *
     * @param dataInitializer the component used to initialize the state table data
     * @return a reference to this builder
     */
    @NotNull
    StateTableDataManagerBuilder<D, E> withInitializer(@NotNull Runnable dataInitializer);

    /**
     * Sets the function that returns the state table data from the event
     *
     * @param dataGetterFunction the function that returns the state table data from the event
     * @return a reference to this builder
     */
    @NotNull
    StateTableDataManagerBuilder<D, E> withDataGetter(@NotNull Function<E, D> dataGetterFunction);

    /**
     * Sets the optional consumer provided when the updated state table data needs to be set back into a store or the
     * working copy
     *
     * @param dataSetterConsumer the consumer provided when the updated state table data needs to be set back into a store
     *                           or the working copy
     * @return a reference to this builder
     */
    @NotNull
    StateTableDataManagerBuilder<D, E> withDataSetter(@NotNull BiConsumer<E, D> dataSetterConsumer);

    /**
     * Return the state table builder that launched this builder after setting the state table manager
     *
     * @return the state table builder that launched this builder
     * @throws StateDefException thrown when something is missing when building the state table data manager
     */
    @NotNull
    StateTableBuilder<D, E> endDataManager() throws StateDefException;

    /**
     * Returns the built state table manager
     *
     * @return the built state table manager
     * @throws StateDefException thrown when a required element is missing
     */
    @NotNull
    StateTableDataManager<D, E> build() throws StateDefException;
}
