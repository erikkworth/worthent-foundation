package com.worthent.foundation.util.state.provider;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateExeException;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.TransitionActor;
import com.worthent.foundation.util.state.TransitionContext;
import com.worthent.foundation.util.state.def.StateDef;
import com.worthent.foundation.util.state.def.impl.ToStateCondition;

import java.util.List;
import java.util.Optional;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * This Transition Actor is designed to be used in a state transition where an actor is designated to determine the
 * destination state.  This is used on conjunction with a list of {@link ToStateCondition} instances that associate
 * a target state with a condition such that when the condition is satisfied, this class directs the state table to the
 * target state.  It is typically added to the end of the list of state transition actors on transition definition.
 *
 * @author Erik K. Worth
 */
public class ToStateNavigationActor<D extends StateTableData, E extends StateEvent> implements TransitionActor<D, E> {

    private final List<ToStateCondition<D, E>> toStateConditions;

    public ToStateNavigationActor(@NotNull final List<ToStateCondition<D, E>> toStateConditions) {
        this.toStateConditions = checkNotNull(toStateConditions, "toStateConditions must not be null");
    }

    @Override
    public String getName() {
        return "ToStateNavigationActor";
    }

    @Override
    public void onAction(@NotNull final TransitionContext<D, E> context) throws StateExeException {
        // Test the conditions to find the first one that is satisfied
        final Optional<ToStateCondition<D, E>> satisfiedCondition =
                toStateConditions.stream().filter(c -> c.test(context)).findFirst();
        if (!satisfiedCondition.isPresent()) {
            throw new StateExeException("No condition satisfied to transition to next state");
        }
        final StateTableData dataObject = context.getStateTableData();
        // Go to the state associated with the satisfied condition
        String goToState = satisfiedCondition.get().getToState();
        if (StateDef.STAY_IN_STATE.equalsIgnoreCase(goToState)) {
            goToState = dataObject.getCurrentState();
        }
        dataObject.setPriorState(dataObject.getCurrentState());
        dataObject.setCurrentState(goToState);
    }
}
