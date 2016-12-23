/**
 * Copyright 2000-2015 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.impl;

import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateEventBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements the builder interface for state events.
 */
public class StateEventBuilderImpl implements StateEventBuilder {

    /** The name of the event being built */
    private final String eventName;

    /** The event data values */
    private final Map<String, Object> eventData;

    /** Construct with the event name */
    public StateEventBuilderImpl(final String eventName) {
        if (null == eventName) {
            throw new IllegalArgumentException("eventName must not be null");
        }
        if (eventName.trim().length() == 0) {
            throw new IllegalArgumentException("eventName must not be blank");
        }
        this.eventName = eventName;
        this.eventData = new HashMap<>();
    }

    @Override
    public String getName() {
        return eventName;
    }

    @Override
    @SuppressWarnings("Unchecked")
    public <T> T getEventData(final String key) {
        return (T) eventData.get(key);
    }

    @Override
    public Map<String, Object> getEventData() {
        return eventData;
    }

    @Override
    public StateEventBuilder withEventData(final String name, final Object value) {
        eventData.put(name, value);
        return this;
    }

    @Override
    public StateEvent build() {
        return new StateEventWithDataMapImpl(eventName, eventData);
    }
}
