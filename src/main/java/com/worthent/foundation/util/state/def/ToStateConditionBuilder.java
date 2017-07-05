package com.worthent.foundation.util.state.def;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Builder used to build a {@link com.worthent.foundation.util.state.def.impl.ToStateCondition} that can drive
 * the state table to a new state based on a condition of the state table data or the data and the received event.
 *
 * @author Erik K. Worth
 */
public interface ToStateConditionBuilder<D extends StateTableData, E extends StateEvent> {

    /**
     * Creates a ToStateCondition based on the provided predicate that evaluates the state table data and returns
     * the builder for lists of To State Conditions.
     *
     * @param conditionOnData the predicate that operates on the state table data to determine the target state
     * @return a reference to this builder
     */
    ToStateConditionListBuilder<D, E> when(@NotNull Predicate<D> conditionOnData);

    /**
     * Creates a ToStateCondition based on the provided predicate that evaluates the state table data and received
     * event, and returns the builder for lists of To State Conditions.
     *
     * @param conditionOnDataAndEvent two-argument predicate that takes both the data object and event to determine the
     *                                target state
     * @return a reference to this builder
     */
    ToStateConditionListBuilder<D, E> when(@NotNull BiPredicate<D, E> conditionOnDataAndEvent);
}
