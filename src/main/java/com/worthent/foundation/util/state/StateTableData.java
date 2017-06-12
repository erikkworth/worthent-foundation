/*
 * Copyright 2000-2011 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state;

/**
 * Specifies the minimum set of operations required to hold the data state of a
 * state table
 *
 * @see StateTable
 *
 * @author Erik K. Worth
 */
public interface StateTableData {

    /** Returns the current state of the state table instance */
    String getCurrentState();

    /** Sets the current state of the state table instance */
    void setCurrentState(String currentState);

    /** Returns the prior state of the state table instance */
    String getPriorState();

    /** Sets the prior state of the state table instance */
    void setPriorState(String priorState);

}
