package com.worthent.foundation.util.state.etc.xml;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.StateEvent;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Specifies the events produced by the SAX Event Adapter.
 *
 * @author Erik K. Worth
 */
public class XmlEvent implements StateEvent {

    /** The type of event */
    private final String eventName;

    protected XmlEvent(@NotNull final String eventName) {
        this.eventName = checkNotNull(eventName, "eventName must not be null");
    }

    @Override
    public String getName() {
        return eventName;
    }

    @Override
    public String toString() {
        return getName();
    }
}
