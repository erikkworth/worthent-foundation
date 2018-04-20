package com.worthent.foundation.util.state.examples.turnstyle;

import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateEvents;
import com.worthent.foundation.util.state.StateTable;
import com.worthent.foundation.util.state.def.StateDef;
import com.worthent.foundation.util.state.def.StateTransitionDefs;
import com.worthent.foundation.util.state.impl.StateTableBuilderImpl;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class TurnstileStateTable {

    static final StateEvent ON_EVENT = StateEvents.enumeratedStateEvent(TurnstileEventType.ON);
    static final StateEvent PUSH_EVENT = StateEvents.enumeratedStateEvent(TurnstileEventType.PUSH);
    static final StateEvent TICKET_EVENT = StateEvents.enumeratedStateEvent(TurnstileEventType.TICKET);
    static final StateEvent OFF_EVENT = StateEvents.enumeratedStateEvent(TurnstileEventType.OFF);

    /** State table data */
    private TurnstileData stateTableData;

    /** State table representing a turnstile like you find in amusement parks */
    private final StateTable<TurnstileData, StateEvent> turnstileStateTable;

    TurnstileStateTable(final Queue<String> stateQueue) {
        this.turnstileStateTable =
                new StateTableBuilderImpl<TurnstileData, StateEvent>()
                        .withStateTableDefinition()
                        .setName("Turnstile")
                        .usingActorsInClass(TurnstileData.class)
                        .withState(TurnstileStates.OFF)
                            .transitionOnEvent(TurnstileEventType.ON).toState(TurnstileStates.LOCKED).endTransition()
                            .withDefaultEventHandler().toState(StateDef.STAY_IN_STATE).endTransition()
                            .endState()
                        .withState(TurnstileStates.LOCKED)
                            .transitionOnEvent(TurnstileEventType.TICKET)
                                .toState(TurnstileStates.UNLOCKED)
                                .withActorsByName(TurnstileData.INCREMENT_COUNT)
                                .endTransition()
                            .transitionOnEvent(TurnstileEventType.PUSH).toState(StateDef.STAY_IN_STATE).endTransition()
                            .transitionOnEvent(TurnstileEventType.OFF).toState(TurnstileStates.OFF).endTransition()
                            .withDefaultEventHandler(StateTransitionDefs.getUnexpectedEventDefaultTransition())
                            .endState()
                        .withState(TurnstileStates.UNLOCKED)
                            .transitionOnEvent(TurnstileEventType.TICKET).toState(StateDef.STAY_IN_STATE).endTransition()
                            .transitionOnEvent(TurnstileEventType.PUSH)
                                .toState(TurnstileStates.LOCKED)
                                .withActorsByName(TurnstileData.INCREMENT_COUNT)
                                .endTransition()
                            .transitionOnEvent(TurnstileEventType.OFF).toState(TurnstileStates.OFF).endTransition()
                            .withDefaultEventHandler(StateTransitionDefs.getUnexpectedEventDefaultTransition())
                            .endState()
                        .endDefinition()
                        .withStateTableDataManager()
                            .withInitializer(() -> stateTableData = new TurnstileData(stateQueue))
                            .withDataGetter((e) -> new TurnstileData(stateTableData))
                            .withDataSetter((e, updatedData) -> stateTableData.set(updatedData))
                            .endDataManager()
                        .build();
    }

    public TurnstileData getStateTableData() {
        return stateTableData;
    }

    public StateTable<TurnstileData, StateEvent> getTurnstileStateTable() {
        return turnstileStateTable;
    }

    static void assertExpectedState(final Queue<String> stateQueue, final TurnstileStates expectedState) {
        final String actualState = stateQueue.remove();
        assertThat(actualState).isEqualToIgnoringCase(expectedState.name());
    }

    static void assertExpectedState(
            final BlockingQueue<String> stateQueue,
            final TurnstileStates expectedState) throws InterruptedException {
        final String actualState = stateQueue.poll(2, TimeUnit.SECONDS);
        assertThat(actualState).isEqualToIgnoringCase(expectedState.name());
    }

}
