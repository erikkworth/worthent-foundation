/*
 * Copyright 2000-2011 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state;

/**
 * A simple JavaBean holding the current and prior state of a state table
 * instance.
 * 
 * @see StateTable
 * 
 * @author Erik K. Worth
 * @version $Id: AbstractStateTableData.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
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

    /** Construct from components */
    public AbstractStateTableData(final String currentState, final String priorState) {
        this.currentState = currentState;
        this.priorState = priorState;
    }

    /** Copy constructor */
    public AbstractStateTableData(final AbstractStateTableData other) {
        this.currentState = other.currentState;
        this.priorState = other.priorState;
    }

    /** Sets the history from another instance */
    public void set(final AbstractStateTableData other) {
        this.currentState = other.currentState;
        this.priorState = other.priorState;
    }

    /** Returns the current state of the state table instance */
    public final String getCurrentState() {
        return currentState;
    }

    /** Sets the current state of the state table instance */
    public final void setCurrentState(final String currentState) {
        this.currentState = currentState;
    }

    /** Returns the prior state of the state table instance */
    public final String getPriorState() {
        return priorState;
    }

    /** Sets the prior state of the state table instance */
    public final void setPriorState(final String priorState) {
        this.priorState = priorState;
    }

}
