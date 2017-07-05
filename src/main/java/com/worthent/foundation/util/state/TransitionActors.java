package com.worthent.foundation.util.state;

import com.worthent.foundation.util.state.provider.UnexpectedEventActor;

/**
 * Helper class used to provide instances of Reusable state transition actors.
 *
 * @author Erik K. Worth
 */
public class TransitionActors {

    /** Returns a transition actor that can be used to handle unexpected events by throwing an exception when
     * the provided event is not handled by any other actor.  This is typically used as the last registered handler
     * or the default handler for a state transition.
     * @param <D> the state table data type
     * @param <E> the event type
     * @return a transition actor that can be used to handle unexpected events by throwing an exception when
     * the provided event is not handled by any other actor
     */
    public static <D extends StateTableData, E extends StateEvent> TransitionActor<D, E> unexpectedEventActor() {
        return new UnexpectedEventActor<>();
    }
}
