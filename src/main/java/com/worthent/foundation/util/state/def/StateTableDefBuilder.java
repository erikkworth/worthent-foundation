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
     *
     * @param name the state table name
     * @return a reference to this builder
     */
    @NotNull
    StateTableDefBuilder<D, E> setName(@Nullable String name);

    /**
     * Use the actors annotated in the provided class.
     *
     * @param annotatedClass the class with the @Actor tags identifying methods that do work during state transitions
     * @return a reference to this builder
     */
    @NotNull
    StateTableDefBuilder<D, E> usingActorsInClass(@NotNull Class<?> annotatedClass);

    /**
     * Returns a new builder for a state in the state table with the provided name
     *
     * @param stateName the name of the state to start defining
     * @return a reference to the state definition builder
     */
    @NotNull
    StateDefBuilder<D, E> withState(@NotNull String stateName);

    /**
     * Returns a new builder for a state in the state table with the provided name
     *
     * @param state the state to start defining
     * @return a reference to the state definition builder
     */
    @NotNull
    StateDefBuilder<D, E> withState(@NotNull Enum<?> state);

    /**
     * Appends a state definition to the state table
     *
     * @param state the state definition to append to the state table
     * @return a reference to this builder
     */
    @NotNull
    StateTableDefBuilder<D, E> appendState(@NotNull StateDef<D, E> state);

    /**
     * Returns the state table builder that launched this builder after setting the state table definition
     * @return a reference to the parent of this builder, the state table builder
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
