/*
 * Copyright 2000-2011 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state.impl;

import java.util.List;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.worthent.foundation.util.state.def.StateDef;
import com.worthent.foundation.util.state.def.StateTableDef;
import com.worthent.foundation.util.state.def.StateTransitionDef;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Orchestrates the activities involved when receiving an event while in a given
 * state that results in the transition to the next state. The single instance
 * of this class has no state and exposes a single thread-safe method,
 * {@link #processEvent} that performs the following steps:
 * <ol>
 * <li>Retrieves the state table metadata from the
 * {@link com.worthent.foundation.util.state.StateTable}.
 * <li>Retrieves the current and prior states from the
 * {@link com.worthent.foundation.util.state.StateTable}.
 * <li>Identifies the state transition based on the current state and the event.
 * <li>Identifies the state to which the table will transition if there are no
 * errors.
 * <li>Invokes the
 * {@link com.worthent.foundation.util.state.TransitionActor}s in
 * order as configured for the state transition
 * <li>After all of the <code>TransitionActor</code>s are invoked successfully,
 * it invokes the configured
 * {@link com.worthent.foundation.util.state.StateTransitioner}.
 * <li>Finally, it updates the new current and prior states to the
 * {@link com.worthent.foundation.util.state.StateTable}.
 * </ol>
 * If there is an error at any point, it invokes the registered
 * {@link com.worthent.foundation.util.state.StateErrorHandler} (if
 * any), and throws
 * {@link com.worthent.foundation.util.state.StateExeException}.
 * <p>
 * Providers invoke this from the object that consumes events. Providers are
 * expected to establish any required transaction context prior to invoking the
 * {@link #processEvent} method. If the operation returns without error,
 * providers are expected to commit any transactions. If the operation throws an
 * exception, providers are expected to roll back any transactions.
 * 
 * @author Erik K. Worth
 * @version $Id: StateEngine.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
 */
public final class StateEngine<D extends StateTableData, E extends StateEvent> {

    /** Logger for this class */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(StateEngine.class);

    /**
     * The constant indicating a state or event is "Unknown" for logging
     * purposes
     */
    private static final String UNKNOWN = "<Unknown>";

    /** Construct an instance of the engine. */
    public StateEngine() {
    }

    /**
     * Processes the event on the configured state table from the specified
     * state and returns the new state of the state table.
     * 
     * @param table the definition of the state table
     * @param stateTableControl the control object that feeds events into the state table
     * @param event the event being applied to the state table to trigger a
     *        transition
     * 
     * @exception StateExeException thrown when an error prevented the state
     *            table from transitioning to the new state. The caller is
     *            expected to roll back any transactions
     */
    public void processEvent(
        @NotNull final StateTable<D, E> table,
        @NotNull final StateTableControl<E> stateTableControl,
        @NotNull final E event) throws StateExeException {
        checkNotNull(table, "table must not be null");
        checkNotNull(stateTableControl, "stateTableControl must not be null");
        checkNotNull(event, "event must not be null");

        // Get the state table metadata
        final StateTableDef<D, E> metadata = table.getStateTableDefinition();

        // Get a copy of the state history from the state table instance
        final D history;
        try {
            history = table.getStateTableDataManager().getStateTableData(event);
        } catch (Exception err) {
            throw new StateExeException(
                "Error retrieving the state table history from the event, '" +
                    event.getName() +
                    "'.",
                err);
        }

        // Shortcuts for current and prior states
        final String currentState = history.getCurrentState();
        final String priorState = history.getPriorState();

        LOGGER.trace("State table, {}, processing event, {}, while in state, {}" +
                metadata.getName(), event, currentState);

        try {
            // Retrieve the current state
            final StateDef<D, E> state = metadata.getState(currentState);
            if (null == state) {
                throw new StateExeException("The state table, '" +
                        metadata.getName() +
                        "', does not contain a state definition for the state, '" +
                        currentState +
                        "'.");
            }

            // Get the transition referenced by the event name or if not found,
            // get the default
            StateTransitionDef<D, E> transition = state.getTransitionForEvent(event.getName());
            if (null == transition) {
                transition = state.getDefaultTransition();
            }

            // Figure out the target state
            String targetState = transition.getTargetStateName();
            if (StateDef.STAY_IN_STATE.equals(targetState)) {
                targetState = currentState;
            } else if (StateDef.GOTO_PREVIOUS_STATE.equals(currentState)) {
                targetState = priorState;
            }

            // Create a transition context made available to the transition actors
            final TransitionContextImpl<D, E> transitionContext =
                    new TransitionContextImpl<>(currentState, targetState, table, history, stateTableControl, event);

            // Get the set of actions to take on this transition
            final List<TransitionActor<D, E>> actors = transition.getActors();
            for (final TransitionActor<D, E> actor : actors) {
                try {
                    actor.onAction(transitionContext);
                } catch (Exception exc) {

                    // Let the registered error handler do something
                    this.invokeErrorHandler(transitionContext, actor, exc);

                    final String actorName = actor.getName();
                    throw new StateExeException("The actor, '" +
                            actorName +
                            "', encountered an error in state table, '" +
                            metadata.getName() +
                            "', when processing the event, '" +
                            event.getName() +
                            "', while in state, '" +
                            currentState +
                            "'", exc);
                }
            } // for each actor

            // Get the registered transition handler
            StateTransitioner<D, E> transitioner = table.getTransitioner();
            if (null != transitioner) {
                try {
                    // Act on the state transition
                    transitioner.onTransition(transitionContext);
                } catch (Exception exc) {
                    // Let the registered error handler do something
                    this.invokeErrorHandler(transitionContext, null, exc);
                    throw new StateExeException("The transitioner, '" +
                            transitioner.getName() +
                            "', encountered an error in state table, '" +
                            metadata.getName() +
                            "', when processing the event, '" +
                            event.getName() +
                            "', while in state, '" +
                            currentState +
                            "'", exc);
                }
            } // if there is a transitioner registered

            if (!StateDef.STATE_CHANGE_BY_ACTOR.equals(targetState)) {
                // Update the state table history to reflect the new state
                // unless the transition action is taking care of the change
                history.setCurrentState(targetState);
                history.setPriorState(currentState);
            }

            // Everything worked. Update the state table instance with the
            // new and prior state.
            try {
                table.getStateTableDataManager().setStateTableData(event, history);
            } catch (Exception exc) {

                // Let registered error handler do something
                this.invokeErrorHandler(transitionContext, null, exc);

                throw new StateExeException(
                        "There was an error updating the state table history in state table, '" +
                                metadata.getName() +
                                "', when processing the event, '" +
                                event.getName() +
                                "', while in state, '" +
                                currentState +
                                "'",
                        exc);
            }
        } catch (final StateExeException exc) {
            // The error handler was already run
            throw exc;
        } catch (RuntimeException exc) {
            // Let registered error handler do something
            this.invokeErrorHandler(
                    new TransitionContextImpl<>(currentState, UNKNOWN, table, history, stateTableControl, event),
                    null,
                    exc);
            throw new StateExeException(
                "There was an error in the state table, '" +
                    metadata.getName() +
                    "', when processing the event, '" +
                    event.getName() +
                    "', while in state, '" +
                    currentState +
                    "'",
                exc);
        }
    } // processEvent

    /**
     * Invokes the error handler if it is not <code>null</code>.
     * 
     * @param context the context for the state transition
     * @param actor the transition actor if applicable
     * @param cause the cause of the error
     */
    private void invokeErrorHandler(
            final TransitionContext<D, E> context,
            final TransitionActor<D, E> actor,
            final Exception cause) {
        try {
            StateErrorHandler<D, E> errorHandler = context.getStateTable().getErrorHandler();
            if (null != errorHandler) {
                // Forward to registered error handler
                errorHandler.onError(context, actor, cause);
            }
        } catch (final Exception ignore) {
            // Empty
        }
    } // invokeErrorHandler

}
