package com.worthent.foundation.util.state;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;

import java.util.Map;

/**
 * Extends the basic state event to include some event data in the form of a map of named values.
 *
 * @author Erik K. Worth
 */
public interface StateEventWithDataMap extends StateEvent {

    /**
     * Returns an element of data by its key
     *
     * @param key the identifier for the data item
     * @param <T> the type of the data item
     * @return the data item value identified by the key
     */
    @Nullable
    <T> T getEventData(@Nullable String key);

    /**
     * Returns all the event data
     *
     * @return all the event data
     */
    @NotNull
    Map<String, Object> getEventData();
}
