package com.worthent.foundation.util.state.def.impl;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.def.StateDef;
import com.worthent.foundation.util.state.def.StateTransitionDefBuilder;
import com.worthent.foundation.util.state.def.ToStateConditionBuilder;
import com.worthent.foundation.util.state.def.ToStateConditionListBuilder;

import java.util.LinkedList;
import java.util.List;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Builder used to build a list of {@link com.worthent.foundation.util.state.def.impl.ToStateCondition} used to drive
 * the state table to a new state based on a condition of the state table data or the data and the received event.
 *
 * @author Erik K. Worth
 */
public class ToStateConditionListBuilderImpl<D extends StateTableData, E extends StateEvent>
    extends AbstractChildBuilder<StateTransitionDefBuilderImpl<D, E>>
        implements ToStateConditionListBuilder<D, E> {

    public enum CheckPosition {
        BEFORE_ACTORS,
        AFTER_ACTORS;
    }
    private final CheckPosition checkPosition;
    private final List<ToStateCondition<D, E>> toStateConditions;

    ToStateConditionListBuilderImpl(
            @NotNull final StateTransitionDefBuilderImpl<D, E> parentBuilder,
            @NotNull final CheckPosition checkPosition) {
        super(checkNotNull(parentBuilder, "parentBuilder must not be null"));
        this.checkPosition = checkNotNull(checkPosition, "checkPosition must not be null");
        toStateConditions = new LinkedList<>();
    }

    void append(@NotNull final ToStateCondition<D, E> toStateCondition) {
        toStateConditions.add(toStateCondition);
    }

    @Override
    public ToStateConditionBuilder<D, E> orToState(@NotNull final String toStateName) {
        return new ToStateConditionBuilderImpl<>(this, checkNotNull(toStateName, "toStateName must not be null"));
    }

    public StateTransitionDefBuilder<D, E> elseFail() {
        // The resulting actor fails when none of the conditions are satisfied, so we do nothing here.
        final StateTransitionDefBuilderImpl<D, E> parentBuilder = getParentBuilder();
        parentBuilder.setToStateConditions(toStateConditions, checkPosition);
        return parentBuilder;
    }

    public StateTransitionDefBuilder<D, E> elseStayInState() {
        final StateTransitionDefBuilderImpl<D, E> parentBuilder = getParentBuilder();
        // Add a condition that always returns true and where the To State directs it to stay in the current state
        toStateConditions.add(() -> StateDef.STAY_IN_STATE);
        parentBuilder.setToStateConditions(toStateConditions, checkPosition);
        return parentBuilder;
    }

    @Override
    public StateTransitionDefBuilder<D, E> elseGoToState(@NotNull String toStateName) {
        final StateTransitionDefBuilderImpl<D, E> parentBuilder = getParentBuilder();
        // Add a condition that always returns true and where the To State directs it to go to the specified state
        toStateConditions.add(() -> toStateName);
        parentBuilder.setToStateConditions(toStateConditions, checkPosition);
        return parentBuilder;
    }
}
