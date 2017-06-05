package com.worthent.foundation.util.state.def;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;

/**
 * Builder used to build a list of {@link com.worthent.foundation.util.state.def.impl.ToStateCondition} used to drive
 * the state table to a new state based on a condition of the state table data or the data and the received event.
 *
 * @author Erik K. Worth
 */
public interface ToStateConditionListBuilder<D extends StateTableData, E extends StateEvent> {

    /** Returns the To State Condition Builder to capture the predicate used to decide to go to this state or not */
    ToStateConditionBuilder<D, E> orToState(@NotNull String toStateName);

    /** Returns to the state transition builder after making sure an exception is thrown when no condition matches */
    StateTransitionDefBuilder<D, E> elseFail();

    /**
     * Returns to the state transition builder after making sure the state table remains in the current state when no
     * condition matches
     */
    StateTransitionDefBuilder<D, E> elseStayInState();

    /**
     * Returns to the state transition builder after making sure the state table transitions to this state when no
     * other conditions match
     */
    StateTransitionDefBuilder<D, E> elseGoToState(@NotNull String toStateName);
}
