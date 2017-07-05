package com.worthent.foundation.util.state;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;

/**
 * Specifies the methods used to build a new State Event to submit to a State Table.
 *
 * @author Erik K. Worth
 */
public interface StateEventBuilder extends StateEventWithDataMap {

    /**
     * Appends a data item to state event
     *
     * @param name the key identifying the data item
     * @param value the data item's value
     * @return the instance of the builder
     */
    @NotNull
    StateEventBuilder withEventData(@NotNull String name, @Nullable Object value);

    /**
     * Returns a new instance of the state event.
     *
     * @return a new instance of the state event
     */
    @NotNull
    StateEvent build();
}
