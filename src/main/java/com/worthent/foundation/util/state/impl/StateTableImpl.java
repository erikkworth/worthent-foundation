/*
 * Copyright 2000-2011 Worth Enterprises, Inc. All Rights Reserved.
 */
package com.worthent.foundation.util.state.impl;

import com.worthent.foundation.util.state.StateErrorHandler;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTable;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.data.StateTableDataManager;
import com.worthent.foundation.util.state.StateTransitioner;
import com.worthent.foundation.util.state.provider.LoggingStateErrorHandler;
import com.worthent.foundation.util.state.provider.LoggingStateTransitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.worthent.foundation.util.state.def.StateTableDef;

/**
 * Provides an in-memory implementation of the state table instance.
 * 
 * @author Erik K. Worth
 * @version $Id: StateTableImpl.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
 */
public class StateTableImpl<D extends StateTableData, E extends StateEvent> implements StateTable<D, E> {

    /** Logger used for this class */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(StateTableImpl.class);

    /** The state table error handler to use */
    private final StateErrorHandler errorHandler;

    /** The state transitioner to use */
    private final StateTransitioner<D, E> stateTransitioner;

    /** The state table definition */
    private final StateTableDef<D, E> stateTblDef;

    /** Keeps track of current and prior state table state */
    private final StateTableDataManager<D, E> stateTableDataManager;

    /**
     * Constructs the state table instance with the table definition, data manager, error handler and transitioner.
     *
     * @param stateTblDef the state table definition
     * @param stateTableDataManager the object able to access the data object
     * @param errorHandler the error handler able to act on exceptions during event processing or <code>null</code>
     *                     to use the default error handler that simply writes messages to the logger
     * @param stateTransitioner the object that is invoked for each successful state transition or <code>null</code>
     *                          to use the default state transitioner that simply writes messages to the logger
     */
    StateTableImpl(
            final StateTableDef<D, E> stateTblDef,
            final StateTableDataManager<D, E> stateTableDataManager,
            final StateErrorHandler errorHandler,
            final StateTransitioner<D, E> stateTransitioner) {
        if (null == stateTblDef) {
            throw new IllegalArgumentException("stateTableDef must nto be null");
        }
        if (null == stateTableDataManager) {
            throw new IllegalArgumentException("stateTableDataManager must nto be null");
        }
        this.stateTblDef = stateTblDef;
        this.stateTableDataManager = stateTableDataManager;
        this.errorHandler = (null == errorHandler) ? new LoggingStateErrorHandler(LOGGER) : errorHandler;
        this.stateTransitioner = (null == stateTransitioner) ? new LoggingStateTransitioner<>(LOGGER) : stateTransitioner;
    }

    //
    // StateTable
    //

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.worthent.foundation.core.util.state.StateTable#getStateTableName()
     */
    @Override
    public String getStateTableName() {
        return stateTblDef.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.worthent.foundation.core.util.state.StateTable#getErrorHandler()
     */
    @Override
    public StateErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public StateTableDataManager<D, E> getStateTableDataManager() {
        return stateTableDataManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.worthent.foundation.core.util.state.StateTable#getTransitioner()
     */
    @Override
    public StateTransitioner<D, E> getTransitioner() {
        return stateTransitioner;
    }

    /** Returns the state table definition */
    @Override
    public StateTableDef<D, E> getStateTableDefinition(final StateEvent event) {
        return stateTblDef;
    }

}
