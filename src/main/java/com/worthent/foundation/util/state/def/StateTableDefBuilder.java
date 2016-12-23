/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.def;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableBuilder;
import com.worthent.foundation.util.state.StateTableData;

/**
 * Specifies the operations available on the builder used to define a state table.
 *
 * @author Erik K. Worth
 */
public interface StateTableDefBuilder<D extends StateTableData, E extends StateEvent> extends StateTableDef<D, E> {

    /**
     * Give the state table a name
     */
    @NotNull
    StateTableDefBuilder<D, E> setName(@Nullable String name);

    /**
     * Use the actors annotated in the provided class.
     */
    @NotNull
    StateTableDefBuilder<D, E> usingActorsInClass(@NotNull Class<?> annotatedClass);

    /**
     * Returns a new builder for a state in the state table with the provided name
     */
    @NotNull
    StateDefBuilder<D, E> withState(@NotNull String stateName);

    /**
     * Appends a state definition to the state table
     */
    @NotNull
    StateTableDefBuilder<D, E> appendState(@NotNull StateDef<D, E> state);

    /**
     * Returns the state table builder that launched this builder after setting the state table definition
     * @throws StateDefException if this builder was not created from a state table builder or when there are
     * missing or invalid items in the state table builder
     */
    @NotNull
    StateTableBuilder<D, E> endDefinition() throws StateDefException;

    /**
     * Returns the constructed state table definition or throws an exception if there are missing or invalid items
     * in the state table builder.
     *
     * @return the constructed state table definition
     * @throws StateDefException thrown when there are missing or invalid items
     *                           in the state table builder
     */
    @NotNull
    StateTableDef<D, E> build() throws StateDefException;
}
