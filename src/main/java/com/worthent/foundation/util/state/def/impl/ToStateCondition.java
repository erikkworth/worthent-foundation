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

    /** @return the state to which the transition will go if the test condition returns <code>true</code> */
    String getToState();

    /**
     * The default implementation of the test condition always returns <code>true</code>.
     *
     * @param transitionContext the context for the state transition
     * @return the result of the test
     */
    default boolean test(TransitionContext<D, E> transitionContext) {return true;}
}
