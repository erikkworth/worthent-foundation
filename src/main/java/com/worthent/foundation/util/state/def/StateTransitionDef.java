package com.worthent.foundation.util.state.def;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.TransitionActor;

import java.util.List;
import java.util.Set;

/**
 * Specifies the information available on a state transition.
 *
 * @author Erik K. Worth
 */
public interface StateTransitionDef<D extends StateTableData, E extends StateEvent> {

    /** Event name used to identify the default event handler for any event not explicitly handled */
    String DEFAULT_HANDLER_EVENT_ID = "#Default#";

    /**
     * @return the event identifier associated with this transition.
     */
    @NotNull
    String getEventName();

    /**
     * @return the identifier for the target state when the transition completes.
     */
    @NotNull
    String getTargetStateName();

    /**
     * @return the identifiers for states to which this transition can possibly go based on evaluation conditions.  It
     * returns the single target state for unconditional transitions.
     */
    @NotNull
    Set<String> getPotentialTargetStateNames();

    /**
     * @return the ordered list of components able to perform actions on this transition for a given event.
     */
    @NotNull
    List<TransitionActor<D, E>> getActors();
}
