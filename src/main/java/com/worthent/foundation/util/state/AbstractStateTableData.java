/*
 * Copyright 2000-2011 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * A simple JavaBean holding the current and prior state of a state table
 * instance.
 * 
 * @see StateTable
 * 
 * @author Erik K. Worth
 */
public class AbstractStateTableData implements StateTableData {
    
    /** The current ID of the state table state */
    private String currentState;

    /** The ID of the prior state table state */
    private String priorState;

    /** Default constructor */
    public AbstractStateTableData() {
        currentState = null;
        priorState = null;
    }

    /**
     * Construct from components.
     *
     * @param currentState the state of the state table prior to processing an event
     * @param priorState the state of the state table before processing the previous event
     */
    public AbstractStateTableData(@Nullable final String currentState, @Nullable final String priorState) {
        this.currentState = currentState;
        this.priorState = priorState;
    }

    /**
     * Copy constructor
     *
     * @param other the other state table data to copy
     */
    public AbstractStateTableData(@NotNull final AbstractStateTableData other) {
        checkNotNull(other, "other must not be null");
        this.currentState = other.currentState;
        this.priorState = other.priorState;
    }

    /**
     * Sets the history from another instance
     *
     * @param other the other instance of the state table data to copy from
     */
    public void set(@NotNull final AbstractStateTableData other) {
        checkNotNull(other, "other must not be null");
        this.currentState = other.currentState;
        this.priorState = other.priorState;
    }

    /**
     * Returns the current state of the state table instance
     *
     * @return the current state of the state table instance
     */
    @Nullable
    public String getCurrentState() {
        return currentState;
    }

    /**
     * Sets the current state of the state table instance
     *
     * @param currentState set the current state of the state table typically as the result of a completed transition
     */
    public void setCurrentState(@Nullable final String currentState) {
        this.currentState = currentState;
    }

    /**
     * Returns the prior state of the state table instance
     *
     * @return the prior state of the state table instance
     */
    @Nullable
    public String getPriorState() {
        return priorState;
    }

    /**
     * Sets the prior state of the state table instance
     *
     * @param priorState set the prior state of the state table typically from the previous current state as the result
     *                   of a completed transition
     */
    public void setPriorState(@Nullable final String priorState) {
        this.priorState = priorState;
    }

}
