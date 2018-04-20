package com.worthent.foundation.util.state.impl;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;
import com.worthent.foundation.util.state.StateEventBuilder;
import com.worthent.foundation.util.state.StateEventWithDataMap;

import java.util.HashMap;
import java.util.Map;

import static com.worthent.foundation.util.condition.Preconditions.checkNotBlank;
import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Implements the builder interface for state events.
 */
public class StateEventBuilderImpl<E extends StateEventWithDataMap> implements StateEventBuilder<E> {

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
    public <T> T getRequiredEventData(@Nullable final String key) {
        return checkNotNull(getEventData(key), key + " missing from event data");
    }

    @Override
    @NotNull
    public Map<String, Object> getEventData() {
        return eventData;
    }

    @Override
    @NotNull
    public StateEventBuilder<E> withEventData(@NotNull final String name, @Nullable final Object value) {
        checkNotNull(name, "name must not be null");
        eventData.put(name, value);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public E build() {
        final StateEventWithDataMap event = new StateEventWithDataMapImpl(eventName, eventData);
        return (E) event;
    }
}
