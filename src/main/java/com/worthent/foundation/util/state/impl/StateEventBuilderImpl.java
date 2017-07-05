package com.worthent.foundation.util.state.impl;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateEventBuilder;

import java.util.HashMap;
import java.util.Map;

import static com.worthent.foundation.util.condition.Preconditions.checkNotBlank;
import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Implements the builder interface for state events.
 */
public class StateEventBuilderImpl implements StateEventBuilder {

    /** The name of the event being built */
    private final String eventName;

    /** The event data values */
    private final Map<String, Object> eventData;

    /**
     * Construct with the event name
     *
     * @param eventName the identifier for the event
     */
    public StateEventBuilderImpl(@NotNull final String eventName) {
        checkNotBlank(eventName, "eventName must not be blank");
        this.eventName = eventName;
        this.eventData = new HashMap<>();
    }

    @Override
    public String getName() {
        return eventName;
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

    @Override
    @NotNull
    public StateEventBuilder withEventData(@NotNull final String name, @Nullable final Object value) {
        checkNotNull(name, "name must not be null");
        eventData.put(name, value);
        return this;
    }

    @Override
    @NotNull
    public StateEvent build() {
        return new StateEventWithDataMapImpl(eventName, eventData);
    }
}
