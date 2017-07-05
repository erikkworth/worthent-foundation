package com.worthent.foundation.util.state.def.impl;

import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.TransitionActor;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages a collection of transition actors discovered from annotations when classes were scanned
 *
 * @author Erik K. Worth
 */
public class TransitionActorManager<D extends StateTableData, E extends StateEvent> {

    /** The registered transition actors */
    private final Map<String, TransitionActor<D, E>> transitionActors;

    /** Constructs a transition actor manager with an empty map of transition actors */
    public TransitionActorManager() {
        this.transitionActors = new HashMap<>();
    }

    /**
     * Add a transition actor
     *
     * @param transitionActor the transition actor to register by name
     */
    public void addTransitionActor(final TransitionActor<D, E> transitionActor) {
        if (null == transitionActor) {
            throw new IllegalArgumentException("transitionActor must not be null");
        }
        transitionActors.put(transitionActor.getName(), transitionActor);
    }

    /**
     * Returns a transition actor by the provided name or <code>null</code> when none is found by that name
     *
     * @param actorName the name of the transition actor to retrieve
     * @return the transition actor found by the provided name or <code>null</code> when none is found by that name
     */
    public TransitionActor<D, E> getTransitionActor(final String actorName) {
        final TransitionActor<D, E> actor = transitionActors.get(actorName);
        if (null == actor) {
            throw new IllegalStateException("No transition actor found for the name, '" + actorName + "'");
        }
        return actor;
    }
}
