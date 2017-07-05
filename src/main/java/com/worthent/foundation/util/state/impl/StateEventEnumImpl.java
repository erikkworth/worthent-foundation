/*
 * Copyright 2000-2015 Worth Enterprises, Inc. All Rights Reserved.
 */
package com.worthent.foundation.util.state.impl;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.StateEvent;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * State Event where the event types are enumerated.
 * @author Erik K. Worth
 */
public class StateEventEnumImpl<E extends Enum<E>> implements StateEvent {

    /** The enumerated value identifying this event */
    private final E eventType;

    /**
     * Construct with the event type
     *
     * @param eventType the enumerated value identifying this event
     */
    public StateEventEnumImpl(@NotNull final E eventType) {
        this.eventType = checkNotNull(eventType, "eventType must not be null");
    }

    @Override
    public String toString() {
        return "StateEventEnumImpl{" +
                "eventType=" + eventType +
                '}';
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        StateEventEnumImpl<?> that = (StateEventEnumImpl<?>) other;

        return !(eventType != null ? !eventType.equals(that.eventType) : that.eventType != null);

    }

    @Override
    public int hashCode() {
        return eventType != null ? eventType.hashCode() : 0;
    }

    @Override
    public String getName() {
        return eventType.name();
    }
}
