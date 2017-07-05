/*
 * Copyright 2000-2015 Worth Enterprises, Inc. All Rights Reserved.
 */
package com.worthent.foundation.util.state;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.impl.StateEventBuilderImpl;
import com.worthent.foundation.util.state.impl.StateEventEnumImpl;

/**
 * Factory for state table event construction.
 * @author Erik K. Worth
 */
public class StateEvents {

    /**
     * Returns an event from an enumeration of event types
     *
     * @param eventType the enumerated value identifying the event
     * @param <E> the enumerated type
     * @return the event from the enumerated value
     */
    public static <E extends Enum<E>> StateEvent enumeratedStateEvent(@NotNull final E eventType) {
        return new StateEventEnumImpl<E>(eventType);
    }

    /**
     * Return the event builder for the specified event type.
     *
     * @param eventName the event identifier
     * @return the event builder for the specified event type
     */
    @NotNull
    public static StateEventBuilder builder(@NotNull final String eventName) {
        return new StateEventBuilderImpl(eventName);
    }
}
