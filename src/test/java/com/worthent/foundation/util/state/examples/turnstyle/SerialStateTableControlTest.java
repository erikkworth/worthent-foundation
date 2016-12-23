/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.examples.turnstyle;

import com.worthent.foundation.util.state.*;
import com.worthent.foundation.util.state.def.StateDef;
import com.worthent.foundation.util.state.def.StateTransitionDefs;
import com.worthent.foundation.util.state.impl.StateTableBuilderImpl;
import com.worthent.foundation.util.state.provider.SerialStateTableControl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for the builder that defines a state table.
 *
 * @author Erik K. Worth
 */
public class SerialStateTableControlTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerialStateTableControlTest.class);

    private static final StateEvent ON_EVENT = StateEvents.enumeratedStateEvent(TurnstileEventType.ON);
    private static final StateEvent PUSH_EVENT = StateEvents.enumeratedStateEvent(TurnstileEventType.PUSH);
    private static final StateEvent COIN_EVENT = StateEvents.enumeratedStateEvent(TurnstileEventType.COIN);
    private static final StateEvent OFF_EVENT = StateEvents.enumeratedStateEvent(TurnstileEventType.OFF);

    /** State table data */
    private TurnstileData stateTableData;

    /** State table representing a coin-operated turnstile like you find in amusement parks */
    private final StateTable<TurnstileData, StateEvent> turnstileStateTable =
        new StateTableBuilderImpl<TurnstileData, StateEvent>()
            .withStateTableDefinition()
                .setName("Turnstile")
                .usingActorsInClass(TurnstileData.class)
                .withState(TurnstileStates.OFF.name())
                    .transitionOnEvent(TurnstileEventType.ON.name()).toState(TurnstileStates.LOCKED.name()).endTransition()
                    .withDefaultEventHandler().toState(StateDef.STAY_IN_STATE).endTransition()
                    .endState()
                .withState(TurnstileStates.LOCKED.name())
                    .transitionOnEvent(TurnstileEventType.COIN.name())
                        .toState(TurnstileStates.UNLOCKED.name())
                        .withActorsByName(TurnstileData.INCREMENT_COUNT)
                        .endTransition()
                    .transitionOnEvent(TurnstileEventType.PUSH.name()).toState(StateDef.STAY_IN_STATE).endTransition()
                    .transitionOnEvent(TurnstileEventType.OFF.name()).toState(TurnstileStates.OFF.name()).endTransition()
                    .withDefaultEventHandler(StateTransitionDefs.getUnexpectedEventDefaultTransition())
                    .endState()
                .withState(TurnstileStates.UNLOCKED.name())
                    .transitionOnEvent(TurnstileEventType.COIN.name()).toState(StateDef.STAY_IN_STATE).endTransition()
                    .transitionOnEvent(TurnstileEventType.PUSH.name())
                        .toState(TurnstileStates.LOCKED.name())
                        .withActorsByName(TurnstileData.INCREMENT_COUNT)
                        .endTransition()
                    .transitionOnEvent(TurnstileEventType.OFF.name()).toState(TurnstileStates.OFF.name()).endTransition()
                    .withDefaultEventHandler(StateTransitionDefs.getUnexpectedEventDefaultTransition())
                    .endState()
                .endDefinition()
            .withStateTableDataManager()
                .withInitializer(() -> stateTableData = new TurnstileData())
                .withDataGetter((e) -> new TurnstileData(stateTableData))
                .withDataSetter((e, updatedData) -> stateTableData.set(updatedData))
                .endDataManager()
            .build();

    // The controller being tested
    private StateTableControl<StateEvent> stateTableController;

    @Rule
    public TestWatcher watchman= new TestWatcher() {
        @Override
        public void starting(final Description description) {
            LOGGER.debug("Starting test {}", description.getMethodName());
        }
    };

    @Before
    public void setup() throws Exception {
        stateTableController = new SerialStateTableControl<>(turnstileStateTable);
    }

    @Test
    public void testOneTurnstileEntry() throws Exception {
        stateTableController.start();
        stateTableController.signalEvent(ON_EVENT);
        stateTableController.signalEvent(COIN_EVENT);
        stateTableController.signalEvent(PUSH_EVENT);
        stateTableController.signalEvent(OFF_EVENT);

        assertEquals("Expected Turn Count", 1, stateTableData.getTurnCount());
        assertEquals("Expected Coin Count", 1, stateTableData.getCoinCount());
    }

    @Test
    public void testNoEntryWithoutCoin() throws Exception {
        stateTableController.start();
        stateTableController.signalEvent(ON_EVENT);
        stateTableController.signalEvent(PUSH_EVENT);
        stateTableController.signalEvent(PUSH_EVENT);

        assertEquals("Expected Turn Count", 0, stateTableData.getTurnCount());
        assertEquals("Expected Coin Count", 0, stateTableData.getCoinCount());

        stateTableController.signalEvent(COIN_EVENT);
        stateTableController.signalEvent(PUSH_EVENT);
        stateTableController.signalEvent(OFF_EVENT);

        assertEquals("Expected Turn Count", 1, stateTableData.getTurnCount());
        assertEquals("Expected Coin Count", 1, stateTableData.getCoinCount());
    }

    @Test(expected=StateExeException.class)
    public void testUnexpectedOnEventWhileAlreadyOn() throws Exception {
        stateTableController.start();
        stateTableController.signalEvent(ON_EVENT);
        stateTableController.signalEvent(ON_EVENT);
    }

}
