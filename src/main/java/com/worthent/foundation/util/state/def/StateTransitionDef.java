package com.worthent.foundation.util.state.def;

import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.TransitionActor;

import java.util.List;

/**
 * Specifies the information available on a state transition.
 *
 * @author Erik K. Worth
 * @version $Id: StateTransitionDef.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
 */
public interface StateTransitionDef<D extends StateTableData, E extends StateEvent> {

    /** Event name used to identify the default event handler for any event not explicitly handled */
    String DEFAULT_HANDLER_EVENT_ID = "#Default#";

    /**
     * @return the event identifier associated with this transition.
     */
    String getEventName();

    /**
     * @return the identifier for the target state when the transition completes.
     */
    String getTargetStateName();

    /**
     * @return the ordered list of components able to perform actions on this transition for a given event.
     */
    List<TransitionActor<D, E>> getActors();
}
