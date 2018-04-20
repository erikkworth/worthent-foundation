package com.worthent.foundation.util.state;

import com.worthent.foundation.util.annotation.NotNull;

import java.util.Set;

/**
 * Specifies the information available from the context of transitioning from one state to another while processing
 * an event.
 */
public interface TransitionContext<D extends StateTableData, E extends StateEvent> {

    /** @return the current state where the event was received */
    @NotNull
    String getFromState();

    /** @return the state to which the table will transition should everything work out */
    @NotNull
    String getToState();

    /** @return a reference to the state table */
    @NotNull
    StateTable<D, E> getStateTable();

    /** @return a working reference to the state table data */
    @NotNull
    D getStateTableData();

    /** @return the state table controller */
    @NotNull
    StateTableControl<E> getStateTableControl();

    /** @return the event that trigger the state transition */
    @NotNull
    E getEvent();

    /**
     * Returns the potential states to which the state table will transition given that the transition may be based on
     * defined conditions.
     *
     * @return the set of potential states to which the state table will transition
     * @throws StateExeException thrown when there is no transition defined for the event in the "from" state
     */
    @NotNull
    Set<String> getPotentialTargetStates();
}
