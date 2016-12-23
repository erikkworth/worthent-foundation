/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state;

import java.util.Map;

/**
 * Extends the basic state event to include some event data in the form of a map of named values.
 * @author Erik K. Worth
 */
public interface StateEventWithDataMap extends StateEvent {

    /** Returns an element of data by its key */
    <T> T getEventData(String key);

    /** Returns all the event data */
    Map<String, Object> getEventData();
}
