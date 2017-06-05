package com.worthent.foundation.util.state.def.impl;

import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.TransitionContext;

/**
 * Specifies the components able to direct a state transition based on the satisfaction of a condition.
 *
 * @author Erik K. Worth
 */
public interface ToStateCondition<D extends StateTableData, E extends StateEvent> {

    String getToState();

    default boolean test(TransitionContext<D, E> transitionContext) {return true;}
}
