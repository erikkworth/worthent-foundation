/**
 * Copyright 2000-2015 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.impl;

import com.worthent.foundation.util.state.StateEventWithDataMap;

import java.util.Collections;
import java.util.Map;

/**
 * Provides an immutable state event object with event data items.
 */
public class StateEventWithDataMapImpl implements StateEventWithDataMap {
    private final String name;
    private final Map<String, Object> eventData;

    StateEventWithDataMapImpl(final String name, final Map<String, Object> eventData) {
        if (null == name) {
            throw new IllegalArgumentException("Event name must not be null");
        }
        this.name = name;
        this.eventData = Collections.unmodifiableMap(eventData);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public <T> T getEventData(final String key) {
        return (T) eventData.get(key);
    }

    @Override
    public Map<String, Object> getEventData() {
        return eventData;
    }
}
