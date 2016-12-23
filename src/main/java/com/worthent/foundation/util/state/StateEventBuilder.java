/**
 * Copyright 2000-2015 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state;

/**
 * Specifies the methods used to build a new State Event to submit to a State Table
 */
public interface StateEventBuilder extends StateEventWithDataMap {

    /** Appends a data item to state event */
    StateEventBuilder withEventData(String name, Object value);

    /**
     * Returns a new instance of the state event.
     * @Throws IllegalStateException if the event name is blank
     */
    StateEvent build();
}
