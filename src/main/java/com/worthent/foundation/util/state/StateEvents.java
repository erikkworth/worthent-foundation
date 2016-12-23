/*
 * Copyright 2000-2015 Worth Enterprises, Inc. All Rights Reserved.
 */
package com.worthent.foundation.util.state;

import com.worthent.foundation.util.state.impl.StateEventBuilderImpl;
import com.worthent.foundation.util.state.impl.StateEventEnumImpl;

/**
 * Factory for state table event construction.
 * @author Erik K. Worth
 */
public class StateEvents {

    /** Returns an event from an enumeration of event types */
    public static <E extends Enum<E>> StateEvent enumeratedStateEvent(final E eventType) {
        return new StateEventEnumImpl<E>(eventType);
    }

    public static StateEventBuilder builder(final String eventName) {
        return new StateEventBuilderImpl(eventName);
    }
}
