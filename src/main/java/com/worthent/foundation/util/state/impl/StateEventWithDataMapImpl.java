/**
 * Copyright 2000-2015 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.impl;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;
import com.worthent.foundation.util.state.StateEventWithDataMap;

import java.util.Collections;
import java.util.Map;

import static com.worthent.foundation.util.condition.Preconditions.checkNotBlank;
import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Provides an immutable state event object with event data items.
 */
public class StateEventWithDataMapImpl implements StateEventWithDataMap {

    /** The identifier for the event */
    private final String name;

    /** The data carried with the event */
    private final Map<String, Object> eventData;

    /**
     * Construct from elements.
     *
     * @param name the identifier for the event
     * @param eventData the data carried with the event
     */
    StateEventWithDataMapImpl(@NotNull final String name, @NotNull final Map<String, Object> eventData) {
        this.name = checkNotBlank(name, "name must not be blank");
        this.eventData = Collections.unmodifiableMap(checkNotNull(eventData, "eventData must not be null"));
    }

    @Override
    @NotNull
    public String getName() {
        return name;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getEventData(@Nullable final String key) {
        return (T) eventData.get(key);
    }

    @Override
    @NotNull
    public Map<String, Object> getEventData() {
        return eventData;
    }
}
