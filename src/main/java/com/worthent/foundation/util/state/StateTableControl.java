package com.worthent.foundation.util.state;

import com.worthent.foundation.util.annotation.NotNull;

/**
 * Specifies the methods used to signal event to a state table, start and stop
 * the state transition engine.
 * 
 * @author Erik K. Worth
 */
public interface StateTableControl<E extends StateEvent> {

    /**
     * Starts the state transition engine.
     * 
     * @throws StateExeException thrown when there is an error starting the
     *         state table transition engine
     */
    void start() throws StateExeException;

    /**
     * Directs the state transition engine to stop. It may not stop until all of
     * the currently queued events are processed.
     * 
     * @throws StateExeException thrown when there is an error stopping the
     *         state table transition engine
     */
    void stop() throws StateExeException;

    /**
     * Signals an event to the state transition engine. The event is queued for
     * the state engine. Events are processed in the order they are received.
     *
     * @param event the event to submit to the state table
     *
     * @throws StateExeException thrown when there is an error signalling an event
     */
    void signalEvent(@NotNull E event) throws StateExeException;
}
