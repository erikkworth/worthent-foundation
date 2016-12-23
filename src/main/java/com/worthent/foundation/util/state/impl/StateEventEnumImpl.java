/*
 * Copyright 2000-2015 Worth Enterprises, Inc. All Rights Reserved.
 */
package com.worthent.foundation.util.state.impl;

import com.worthent.foundation.util.state.StateEvent;

/**
 * State Event where the event types are enumerated.
 * @author Erik K. Worth
 */
public class StateEventEnumImpl<E extends Enum<E>> implements StateEvent {

    private final E eventType;

    public StateEventEnumImpl(final E eventType) {
        if (null == eventType) {
            throw new IllegalArgumentException("eventType must not be null");
        }
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "StateEventEnumImpl{" +
                "eventType=" + eventType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StateEventEnumImpl<?> that = (StateEventEnumImpl<?>) o;

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
