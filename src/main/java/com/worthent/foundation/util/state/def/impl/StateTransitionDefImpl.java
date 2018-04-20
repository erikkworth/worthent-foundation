package com.worthent.foundation.util.state.def.impl;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.TransitionActor;
import com.worthent.foundation.util.state.def.StateDef;
import com.worthent.foundation.util.state.def.StateTransitionDef;
import com.worthent.foundation.util.state.provider.ToStateNavigationActor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Immutably encapsulates the information for a state transition definition.
 * 
 * @author Erik K. Worth
 */
public class StateTransitionDefImpl<D extends StateTableData, E extends StateEvent> implements StateTransitionDef<D, E> {

    /** The identifier of the event that triggers this state transition */
    private final String onEvent;

    /**
     * The identifier of the state to which the table transitions when
     * successful
     */
    private final String goToState;

    /**
     * The list of {@link TransitionActor} implementations executed during the
     * state transition.
     */
    private final List<TransitionActor<D, E>> actors;

    /**
     * Construct a state transition object. Instances of this object define the
     * event that triggers a state transition to a given state and the actions
     * to perform on the state transition.
     *
     * @param onEvent the name of the event that triggers the transition
     * @param goToState the state this transitions goes to
     * @param actors the actions to take when the transition occurs
     */
    public StateTransitionDefImpl(
            @NotNull final String onEvent,
            @NotNull final String goToState,
            @NotNull final List<TransitionActor<D, E>> actors) {
        this.onEvent = checkNotNull(onEvent, "onEvent must not be null");
        this.goToState = checkNotNull(goToState, "goToState must not be null");
        this.actors = Collections.unmodifiableList(checkNotNull(actors, "actors must not be null"));
    }

    /**
     * Construct a state transition object. Instances of this object define the
     * event that triggers a state transition to a given state and the action
     * to perform on the state transition.
     *
     * @param onEvent the name of the event that triggers the transition
     * @param goToState the state this transitions goes to
     * @param actor the action to take when the transition occurs
     */
    public StateTransitionDefImpl(
            @NotNull final String onEvent,
            @NotNull final String goToState,
            @NotNull final TransitionActor<D, E> actor) {
        this(onEvent, goToState, Collections.singletonList(checkNotNull(actor, "actor must not be null")));
    }

    /**
     * Construct a state transition object. Instances of this object define the
     * event that triggers a state transition to a given state and the action
     * to perform on the state transition.
     *
     * @param onEvent the name of the event that triggers the transition
     * @param goToState the state this transitions goes to
     */
    public StateTransitionDefImpl(
            @NotNull final String onEvent,
            @NotNull final String goToState) {
        this(onEvent, goToState, Collections.emptyList());
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof StateTransitionDef)) {
            return false;
        }

        final StateTransitionDefImpl<?, ?> that = (StateTransitionDefImpl<?, ?>) other;
        return onEvent.equals(that.getEventName()) && goToState.equals(that.getTargetStateName());
    }

    @Override
    public int hashCode() {
        return 31 * onEvent.hashCode() + goToState.hashCode();
    }

    @NotNull
    @Override
    public final String getEventName() {
        return onEvent;
    }

    @NotNull
    @Override
    public final String getTargetStateName() {
        return goToState;
    }

    @NotNull
    @Override
    public Set<String> getPotentialTargetStateNames() {
        return getPotentialTargetStateNames(goToState, actors);
    }

    @NotNull
    @Override
    public final List<TransitionActor<D, E>> getActors() {
        return actors;
    }

    /**
     * Returns the list of potential target states to which the state table may transition.
     *
     * @param goToState the configured target state
     * @param actors the list of actors, one of which might govern the target state
     * @param <D> the state table data type
     * @param <E> the state table event type
     * @return the list of potential target states to which the state table may transition
     */
    static <D extends StateTableData, E extends StateEvent> Set<String> getPotentialTargetStateNames(
            final String goToState,
            final List<TransitionActor<D, E>> actors) {
        if (actors.isEmpty()) {
            return Collections.singleton(goToState);
        }
        if (!StateDef.STATE_CHANGE_BY_ACTOR.equalsIgnoreCase(goToState)) {
            // Conditional state transitions use a special actor to determine the target state.  Thus the target state
            // is always STATE_CHANGE_BY_ACTOR.  If the target state is not this value, then the target state is the
            // only state to which the transition can go.
            return Collections.singleton(goToState);
        }
        final Optional<ToStateNavigationActor<D, E>> toStateNavigationActor = actors.stream()
                .filter(a -> a instanceof ToStateNavigationActor)
                .map(a -> (ToStateNavigationActor<D, E>)a)
                .findFirst();
        // If the special actor is not found, it is still possible for the integrator to have provided their own
        // actor that governs the state change.  In that case, we do not know where it will go.
        return toStateNavigationActor.isPresent()
                ? toStateNavigationActor.get().getPossibleTargetStates()
                : Collections.singleton(goToState);
    }
}
