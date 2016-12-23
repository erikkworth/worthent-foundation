/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.def.impl;

import com.google.common.collect.ImmutableList;
import com.worthent.foundation.util.state.*;
import com.worthent.foundation.util.state.annotation.Actor;
import com.worthent.foundation.util.state.def.*;
import com.worthent.foundation.util.state.examples.turnstyle.TurnstileData;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test suite for state table definitions.
 *
 * @author Erik K. Worth
 */
public class StateTableDefTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateTableDefTest.class);

    private static final String LOCAL_STATIC_ACTOR = "LocalStaticActor";
    private static final String UNACCEPTABLE_ACTOR = "UnacceptableActor";

    @Rule
    public TestWatcher watchman= new TestWatcher() {
        @Override
        public void starting(final Description description) {
            LOGGER.debug("Starting test {}", description.getMethodName());
        }
    };

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Actor(name = LOCAL_STATIC_ACTOR)
    public static void exampleStaticActorMethod(final TransitionContext<TurnstileData, StateEvent> context) {
        LOGGER.debug("Do something in a static actor method");
    }

    @Actor(name = UNACCEPTABLE_ACTOR)
    public void exampleUnacceptableActorMethod(final TransitionContext<TurnstileData, StateEvent> context) {
        LOGGER.debug("Do something in a static actor method");
    }

    @Actor
    public static void exampleUnnamedStaticActorMethod(final TransitionContext<TurnstileData, StateEvent> context) {
        LOGGER.debug("Do something in a static actor method");
    }

    /** Container for transition actors used during the tests */
    private TransitionActorManager<StateTableData, StateEvent> transitionActorManager;

    @Before
    public void setup() throws Exception {
        initMocks(this);
        transitionActorManager = new TransitionActorManager<>();
    }

    @Test
    public void stateTableDefBuilder_twoStatesTest() throws Exception {
        final String stateTableDefName = "TestStateTable";
        final String initialStateName = "InitialState";
        final String endStateName = "EndState";
        final String eventName = "EventName";
        final StateTransitionDef<StateTableData, StateEvent> noActionTransition =
                StateTransitionDefs.getNoActionTransition(eventName, endStateName);
        final StateDef<StateTableData, StateEvent> initialStateDef =
                new StateDefBuilderImpl<>(null, transitionActorManager, initialStateName)
                        .appendStateTransition(noActionTransition)
                        .build();
        final StateDef<StateTableData, StateEvent> endStateDef =
                new StateDefBuilderImpl<>(null, transitionActorManager, endStateName)
                        .appendStateTransition(noActionTransition)
                        .build();
        final List<StateDef<StateTableData, StateEvent>> expectedStates =
                new ImmutableList.Builder<StateDef<StateTableData, StateEvent>>()
                        .add(initialStateDef)
                        .add(endStateDef)
                        .build();
        final StateTableDefBuilderImpl<StateTableData, StateEvent> builder =
                new StateTableDefBuilderImpl<>();
        builder.setName(stateTableDefName)
                .appendState(initialStateDef)
                .appendState(endStateDef);
        assertExpectedStateTableDef(builder, stateTableDefName, expectedStates);
        final StateTableDef<StateTableData, StateEvent> stateTableDef = builder.build();
        assertExpectedStateTableDef(stateTableDef, stateTableDefName, expectedStates);
    }

    @Test
    public void stateTableBuilder_usingActorsInClassTest() throws Exception {
        final TransitionActorManager<TurnstileData, StateEvent> transitionActorManager = new TransitionActorManager<>();
        final StateTableDefBuilderImpl<TurnstileData, StateEvent> builder =
                new StateTableDefBuilderImpl<>(null, transitionActorManager);
        builder.setName("Turnstile").usingActorsInClass(TurnstileData.class);
        final TransitionActor<TurnstileData, StateEvent> incrementActor =
                transitionActorManager.getTransitionActor(TurnstileData.INCREMENT_COUNT);
        assertNotNull("Increment Count Actor", incrementActor);
        assertEquals("Actor Name", TurnstileData.INCREMENT_COUNT, incrementActor.getName());
    }

    @Test
    public void stateTableBuilder_usingNamedStaticActorMethodTest() throws Exception {
        final TransitionActorManager<TurnstileData, StateEvent> transitionActorManager = new TransitionActorManager<>();
        final StateTableDefBuilderImpl<TurnstileData, StateEvent> builder =
                new StateTableDefBuilderImpl<>(null, transitionActorManager);
        builder.setName("Turnstile").usingActorsInClass(this.getClass());
        final TransitionActor<TurnstileData, StateEvent> localStaticActor =
                transitionActorManager.getTransitionActor(LOCAL_STATIC_ACTOR);
        assertNotNull("Local Static Actor", localStaticActor);
        assertEquals("Actor Name", LOCAL_STATIC_ACTOR, localStaticActor.getName());
    }

    @Test
    public void stateTableBuilder_usingUnnamedStaticActorMethodTest() throws Exception {
        final TransitionActorManager<TurnstileData, StateEvent> transitionActorManager = new TransitionActorManager<>();
        final StateTableDefBuilderImpl<TurnstileData, StateEvent> builder =
                new StateTableDefBuilderImpl<>(null, transitionActorManager);
        builder.setName("Turnstile").usingActorsInClass(this.getClass());
        final TransitionActor<TurnstileData, StateEvent> localStaticActor =
                transitionActorManager.getTransitionActor("exampleUnnamedStaticActorMethod");
        assertNotNull("Local Static Actor", localStaticActor);
        assertEquals("Actor Name", "exampleUnnamedStaticActorMethod", localStaticActor.getName());
    }


    @Test
    public void stateTableBuilder_usingUnacceptableActorMethodTest() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("No transition actor found for the name, 'UnacceptableActor'");
        final TransitionActorManager<TurnstileData, StateEvent> transitionActorManager = new TransitionActorManager<>();
        final StateTableDefBuilderImpl<TurnstileData, StateEvent> builder =
                new StateTableDefBuilderImpl<>(null, transitionActorManager);
        builder.setName("Turnstile").usingActorsInClass(this.getClass());
        transitionActorManager.getTransitionActor(UNACCEPTABLE_ACTOR);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void stateTableBuilder_usingActorsInClassWithNullClassTest() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("annotatedClass must not be null");
        final TransitionActorManager<TurnstileData, StateEvent> transitionActorManager = new TransitionActorManager<>();
        final StateTableDefBuilderImpl<TurnstileData, StateEvent> builder =
                new StateTableDefBuilderImpl<>(null, transitionActorManager);
        builder.setName("Turnstile").usingActorsInClass(null);
    }

    @Test
    public void stateTableDef_getTransition() throws Exception {
        final String stateTableDefName = "TestStateTable";
        final String initialStateName = "InitialState";
        final String endStateName = "EndState";
        final String eventName = "EventName";
        final StateTransitionDef<StateTableData, StateEvent> noActionTransition =
                StateTransitionDefs.getNoActionTransition(eventName, endStateName);
        final StateDef<StateTableData, StateEvent> initialStateDef =
                new StateDefBuilderImpl<>(null, transitionActorManager, initialStateName)
                        .appendStateTransition(noActionTransition)
                        .build();
        final StateDef<StateTableData, StateEvent> endStateDef =
                new StateDefBuilderImpl<>(null, transitionActorManager, endStateName)
                        .appendStateTransition(noActionTransition)
                        .build();
        final StateTableDefBuilderImpl<StateTableData, StateEvent> builder =
                new StateTableDefBuilderImpl<>();
        builder.setName(stateTableDefName)
                .appendState(initialStateDef)
                .appendState(endStateDef);
        final StateTableDef<StateTableData, StateEvent> stateTableDef = builder.build();
        final StateTransitionDef<StateTableData, StateEvent> transitionForEvent =
                stateTableDef.getTransition(initialStateName, eventName);
        assertEquals("State Transition", noActionTransition, transitionForEvent);
    }

    @Test
    public void stateTableDef_getTransitionForMissingState() throws Exception {
        thrown.expect(StateExeException.class);
        thrown.expectMessage("The state table, 'TestStateTable', has no state named, 'MissingState'.");
        final String stateTableDefName = "TestStateTable";
        final String initialStateName = "InitialState";
        final String eventName = "EventName";
        final StateTransitionDef<StateTableData, StateEvent> noActionTransition =
                StateTransitionDefs.getNoActionTransition(eventName, StateDef.STAY_IN_STATE);
        final StateDef<StateTableData, StateEvent> initialStateDef =
                new StateDefBuilderImpl<>(null, transitionActorManager, initialStateName)
                        .appendStateTransition(noActionTransition)
                        .build();
        final StateTableDefBuilderImpl<StateTableData, StateEvent> builder =
                new StateTableDefBuilderImpl<>();
        builder.setName(stateTableDefName)
                .appendState(initialStateDef);
        final StateTableDef<StateTableData, StateEvent> stateTableDef = builder.build();
        stateTableDef.getTransition("MissingState", eventName);
    }

    @Test
    public void stateTableDef_getDefaultTransitionForMissingEvent() throws Exception {
        final String stateTableDefName = "TestStateTable";
        final String initialStateName = "InitialState";
        final String eventName = "EventName";
        final StateTransitionDef<StateTableData, StateEvent> noActionTransition =
                StateTransitionDefs.getNoActionTransition(eventName, StateDef.STAY_IN_STATE);
        final StateDef<StateTableData, StateEvent> initialStateDef =
                new StateDefBuilderImpl<>(null, transitionActorManager, initialStateName)
                        .appendStateTransition(noActionTransition)
                        .build();
        final StateTableDefBuilderImpl<StateTableData, StateEvent> builder =
                new StateTableDefBuilderImpl<>();
        builder.setName(stateTableDefName)
                .appendState(initialStateDef);
        final StateTableDef<StateTableData, StateEvent> stateTableDef = builder.build();
        final StateTransitionDef<StateTableData, StateEvent> transitionForEvent =
                stateTableDef.getTransition(initialStateName, "MissingEvent");
        assertEquals("Default Transition", initialStateDef.getDefaultTransition(), transitionForEvent);
    }

    @Test
    public void stateTableDefBuilder_getInitialStateBeforeStatesAreAdded() throws Exception {
        final String stateTableDefName = "Empty";
        thrown.expect(StateDefException.class);
        thrown.expectMessage("No initial state yet for the state table");
        new StateTableDefBuilderImpl<>().setName(stateTableDefName).getInitialState();
    }

    @Test
    public void stateTableDefBuilder_getTransitionForMissingState() throws Exception {
        final String stateTableDefName = "Empty";
        final String initialStateName = "Missing";
        final String eventName = "Missing";
        thrown.expect(StateExeException.class);
        thrown.expectMessage("No state in table with name, 'Missing'");
        new StateTableDefBuilderImpl<>().setName(stateTableDefName).getTransition(initialStateName, eventName);
    }

    @Test
    public void stateTableDefBuilder_getTransitionForMissingTransition() throws Exception {
        final String stateTableDefName = "Empty";
        final String initialStateName = "Initial";
        final String eventName = "Missing";
        thrown.expect(StateExeException.class);
        thrown.expectMessage("No transition found in state, 'Initial' for event, 'Missing'");
        new StateTableDefBuilderImpl<>()
                .setName(stateTableDefName)
                .withState(initialStateName)
                .transitionOnEvent("Not " + eventName).toState(initialStateName).endTransition()
                .endState()
                .getTransition(initialStateName, eventName);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void stateTableDefBuilder_getTransitionWithNullStateName() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("stateName must not be null");
        new StateTableDefBuilderImpl<>().setName("StateTableName").getTransition(null, "EventName");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void stateTableDefBuilder_getTransitionWithNullEventName() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("eventName must not be null");
        new StateTableDefBuilderImpl<>().setName("StateTableName").getTransition("SateName", null);
    }

    private void assertExpectedStateTableDef(
            final StateTableDef<StateTableData, StateEvent> actualStateTableDef,
            final String expectedStateTableDefName,
            final List<StateDef<StateTableData, StateEvent>> expectedStateDefs) {
        assertEquals("State Table Definition Name", expectedStateTableDefName, actualStateTableDef.getName());
        for (final StateDef<StateTableData, StateEvent> stateDef : expectedStateDefs) {
            final String stateName = stateDef.getName();
            assertTrue("Contains State " + stateName, actualStateTableDef.containsState(stateName));
            final StateDef<StateTableData, StateEvent> actualStateDef = actualStateTableDef.getState(stateName);
            assertExpectedStateDef(stateDef, actualStateDef);
        }
        final StateDef<StateTableData, StateEvent> initialStateDef = expectedStateDefs.get(0);
        assertExpectedStateDef(initialStateDef, actualStateTableDef.getInitialState());
    }

    private void assertExpectedStateDef(
            final StateDef<StateTableData, StateEvent> expectedStateDef,
            final StateDef<StateTableData, StateEvent> actualStateDef) {
        assertEquals("State Name", expectedStateDef.getName(), actualStateDef.getName());
        assertThat(actualStateDef.getTransitions()).containsAll(expectedStateDef.getTransitions());
        assertEquals("Default Transition", expectedStateDef.getDefaultTransition(), actualStateDef.getDefaultTransition());
    }

}
