/*
 * Copyright 2000-2011 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state;

import com.worthent.foundation.util.annotation.Nullable;

/**
 * Specifies the minimum set of operations required to hold the data state of a
 * state table
 *
 * @see StateTable
 *
 * @author Erik K. Worth
 */
public interface StateTableData {

    /**
     * Returns the current state of the state table instance
     *
     * @return the current state of the state table instance
     */
    @Nullable
    String getCurrentState();

    /**
     * Sets the current state of the state table instance
     *
     * @param currentState the current state of the state table instance
     */
    void setCurrentState(@Nullable String currentState);

    /**
     * Returns the prior state of the state table instance
     *
     * @return the prior state of the state table instance
     */
    @Nullable
    String getPriorState();

    /**
     * Sets the prior state of the state table instance
     *
     * @param priorState the prior state of the state table instance
     */
    void setPriorState(@Nullable String priorState);

}
