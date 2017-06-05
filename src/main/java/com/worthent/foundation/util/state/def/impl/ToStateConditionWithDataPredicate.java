package com.worthent.foundation.util.state.def.impl;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.TransitionContext;

import java.util.function.Predicate;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Implements the association of a target state with a condition implemented using a predicate on the data object.
 * This is used to drive a state transition to a target state when the predicate returns <code>true</code> for the
 * current state of the data object backing the state table.
 */
public class ToStateConditionWithDataPredicate<D extends StateTableData, E extends StateEvent>
        implements ToStateCondition<D, E> {

    private final String toState;

    private final Predicate<D> conditionOnData;

    ToStateConditionWithDataPredicate(@NotNull final String toState, @NotNull final Predicate<D> conditionOnData) {
        this.toState = checkNotNull(toState, "toState must not be null");
        this.conditionOnData = checkNotNull(conditionOnData, "conditionOnData must not be null");
    }

    @Override
    @NotNull
    public String getToState() {
        return toState;
    }

    @Override
    public boolean test(@NotNull final TransitionContext<D, E> transitionContext) {
        return conditionOnData.test(transitionContext.getStateTableData());
    }


}
