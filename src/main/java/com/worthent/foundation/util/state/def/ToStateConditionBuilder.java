package com.worthent.foundation.util.state.def;

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
     */
    ToStateConditionListBuilder<D, E> when(Predicate<D> conditionOnData);

    /**
     * Creates a ToStateCondition based on the provided predicate that evaluates the state table data and received
     * event, and returns the builder for lists of To State Conditions.
     */
    ToStateConditionListBuilder<D, E> when(BiPredicate<D, E> conditionOnDataAndEvent);
}
