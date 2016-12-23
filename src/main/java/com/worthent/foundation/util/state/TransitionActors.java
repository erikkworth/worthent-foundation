/*
 * Copyright 2000-2011 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state;

import com.worthent.foundation.util.state.provider.UnexpectedEventActor;

/**
 * Helper class used to provide instances of Reusable state transition actors.
 *
 * @author Erik K. Worth
 * @version $Id: UnexpectedEventActor.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
 */
public class TransitionActors {

    /** Returns a transition actor that can be used to handle unexpected events by throwing an exception when
     * the provided event is not handled by any other actor.  This is typically used as the last registered handler
     * or the default handler for a state transition.
     * @param <D> the state table data type
     * @param <E> the event type
     */
    public static <D extends StateTableData, E extends StateEvent> TransitionActor<D, E> unexpectedEventActor() {
        return new UnexpectedEventActor<>();
    }
}
