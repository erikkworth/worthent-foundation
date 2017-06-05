package com.worthent.foundation.util.state.def.impl;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.def.ToStateConditionBuilder;
import com.worthent.foundation.util.state.def.ToStateConditionListBuilder;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Builder used to build a {@link com.worthent.foundation.util.state.def.impl.ToStateCondition} that can drive
 * the state table to a new state based on a condition of the state table data or the data and the received event.
 *
 * @author Erik K. Worth
 */
public class ToStateConditionBuilderImpl<D extends StateTableData, E extends StateEvent>
        implements ToStateConditionBuilder<D, E> {

    private final ToStateConditionListBuilderImpl<D, E> parentBuilder;

    private final String toStateName;

    ToStateConditionBuilderImpl(
            @NotNull final ToStateConditionListBuilderImpl<D, E> parentBuilder,
            @NotNull final String toStateName) {
        this.parentBuilder = checkNotNull(parentBuilder, "parentBuilder must not be null");
        this.toStateName = checkNotNull(toStateName, "toStateName must not be null");
    }

    @Override
    public ToStateConditionListBuilder<D, E> when(@NotNull final Predicate<D> conditionOnData) {
        checkNotNull(conditionOnData, "conditionOnData must not be null");
        parentBuilder.append(new ToStateConditionWithDataPredicate<>(toStateName, conditionOnData));
        return parentBuilder;
    }

    @Override
    public ToStateConditionListBuilder<D, E> when(@NotNull final BiPredicate<D, E> conditionOnDataAndEvent) {
        checkNotNull(conditionOnDataAndEvent, "conditionOnDataAndEvent must not be null");
        parentBuilder.append(new ToStateConditionWithDataAndEventPredicate<>(toStateName, conditionOnDataAndEvent));
        return parentBuilder;
    }
}
