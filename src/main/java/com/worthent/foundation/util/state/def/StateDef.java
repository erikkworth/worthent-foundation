/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.def;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;

import java.util.Collection;

/**
 * This class defines a state in a state transition table.
 * 
 * Each state definition has the string name of the state and a list of
 * transitions out of the state. Each transition is identified by an event (i.e.
 * there is one transition defined for each event expected at the state). The
 * transition indicates which state to transition to after all the actions are
 * carried out. The transition also specifies a list of actions to carry out on
 * this state transition.
 * <p>
 * The state table performs work through the state table actions. There is an
 * StateActor registered for each action string defined in the table (the
 * initialize method checks for this). When the state table sees an event, it
 * finds the current state, looks for the transition corresponding to the event
 * and runs each actor corresponding to the actions in the state transition
 * action list. When all of the actions are run, the state table runs the
 * registered transitioner (if one was installed). If an error is detected (the
 * StateException or a runtime exception is thrown), the state transition is
 * aborted and the error handler is run (if installed) with a message indicating
 * the state of the table, events, action, and the message portion of the thrown
 * exception.
 * <p>
 * If no transition is specified for a given event, the default transition for the
 * given state is run.
 * 
 * @author Erik K. Worth
 */
public interface StateDef<D extends StateTableData, E extends StateEvent> {

    /**
     * State name for the current state. Use this to transition to the the
     * current state when processing an event. The state history is updated to
     * reflect that the prior state is current state.
     */
    String STAY_IN_STATE = "#ThisState#";

    /**
     * State name for the previous state. Use this to transition to whatever the
     * previous state was after successfully processing an event.
     */
    String GOTO_PREVIOUS_STATE = "#PreviousState#";

    /**
     * State name used to tell the engine to refrain from updating the state in
     * the history. This state is designated in state transitions where the
     * actor explicitly modifies the state table state during event processing.
     */
    String STATE_CHANGE_BY_ACTOR = "#StateChangeByActor#";

    /**
     * @return the identifier for this state.
     */
    @NotNull
    String getName();

    /**
     * @return the <code>StateTransitionDef</code> that originate from this state.
     */
    @NotNull
    Collection<StateTransitionDef<D, E>> getTransitions();

    /**
     * Returns the <code>StateTransitionDef</code> that handles the transition
     * for the specified event name or <code>null</code> if no transition is
     * configured for the specified event name.
     *
     * @param eventName the event identifying the transition definition for this state
     * @return the <code>StateTransitionDef</code> that handles the transition
     * for the specified event name or <code>null</code> if no transition is
     * configured for the specified event name
     */
    @Nullable
    StateTransitionDef<D, E> getTransitionForEvent(@NotNull final String eventName);

    /**
     * @return the state transition defined as the default when the received event matches no transition.
     */
    @NotNull
    StateTransitionDef<D, E> getDefaultTransition();

}
