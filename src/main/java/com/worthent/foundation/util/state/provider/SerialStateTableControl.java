package com.worthent.foundation.util.state.provider;

import java.util.LinkedList;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.*;
import com.worthent.foundation.util.state.impl.StateEngine;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Implements the {@link StateTableControl} interface to provide a serial
 * (non-concurrent) implementation of the state table. The state table
 * transition actions are all conducted on the same thread as the client and the
 * call to the {@link #signalEvent} method does not return until the event has been
 * processed.
 * 
 * @author Erik K. Worth
 */
public class SerialStateTableControl<D extends StateTableData, E extends StateEvent> implements StateTableControl<E> {

    /** The state table engine that processes events */
    private final StateEngine<D, E> engine;

    /** The state table instance */
    private final StateTable<D, E> stateTblInstance;

    /** Event queue */
    private final LinkedList<E> queue;

    /**
     * Constructs the state table controller with the state table instance.
     * 
     * @param stateTblInstance state table instance
     */
    public SerialStateTableControl(final StateTable<D, E> stateTblInstance) {
        this.stateTblInstance = stateTblInstance;
        queue = new LinkedList<>();
        engine = new StateEngine<>();
    }

    /**
     * Queues an event but does not process it. This method should only be
     * called when an actor needs to queue another event.
     * 
     * @param event the event to trigger activity in the state table
     * 
     * @exception StateExeException thrown when there is an error queuing the
     *            event
     * 
     */
    public void queueEvent(@NotNull final E event) {
        checkNotNull(event, "event must not be null");
        // Put this new event into the end of the queue
        queue.addLast(event);
    }

    //
    // StateTableControl
    //

    /**
     * Processes each event and blocks until the event is completely processed.
     * 
     * @param event the event to trigger activity in the state table
     * 
     * @exception StateExeException thrown when there is an error processing the event
     */
    @Override
    public void signalEvent(@NotNull final E event) throws StateExeException {
        checkNotNull(event, "event must not be null");
        // Put this new event into the end of the queue
        queue.addLast(event);

        // Consume the events from the front of the queue until there are no
        // more. Note that the queue is required because the code that
        // processes an event can submit new events back into the table.
        while (!queue.isEmpty()) {
            final E nextEvent = queue.removeFirst();
            engine.processEvent(stateTblInstance, this, nextEvent);
        }
    }

    /**
     * Sets the state table state to the initial state.
     */
    @Override
    public void start() throws StateExeException {
        try {
            stateTblInstance.getStateTableDataManager().initializeStateTableData();
        } catch (Exception exc) {
            final String name = stateTblInstance.getStateTableName();
            throw new StateExeException(
                "Error initializing state table history for state table, " +
                    name);
        }
    }

    /**
     * Since there are no threads or external resources, this method does
     * nothing.
     */
    @Override
    public void stop() throws StateExeException {
    }

}
