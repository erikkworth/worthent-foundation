/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.def.impl;

import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.def.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test suite for state definitions.
 *
 * @author Erik K. Worth
 */
public class StateDefTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateDefTest.class);

    @Rule
    public TestWatcher watchman= new TestWatcher() {
        @Override
        public void starting(final Description description) {
            LOGGER.debug("Starting test {}", description.getMethodName());
        }
    };

    /** Container for transition actors used during the tests */
    private TransitionActorManager<StateTableData, StateEvent> transitionActorManager;

    @Before
    public void setup() throws Exception {
        initMocks(this);
        transitionActorManager = new TransitionActorManager<>();
    }

    @Test
    public void stateBuilder_oneTransitionTest() throws Exception {
        final String startState = "TestStartState";
        final String eventName = "TestEvent";
        final String endState = "TestEndState";
        final StateDefBuilder<StateTableData, StateEvent> builder =
                new StateDefBuilderImpl<>(null, transitionActorManager, startState)
                        .transitionOnEvent(eventName).toState(endState).endTransition();
        final StateDef<StateTableData, StateEvent> actualStateDef = builder.build();
        assertExpectedStateDef(actualStateDef, startState, builder.getTransitions(),
                StateTransitionDefs.getUnexpectedEventDefaultTransition());
    }

    @Test
    public void stateBuilder_oneTransitionWithDefaultTransitionTest() throws Exception {
        final String startState = "TestStartState";
        final String eventName = "TestEvent";
        final String endState = "TestEndState";
        final StateTransitionDef<StateTableData, StateEvent> defaultTransition =
                StateTransitionDefs.getNoActionDefaultTransition();
        final StateDefBuilder<StateTableData, StateEvent> builder =
                new StateDefBuilderImpl<>(null, transitionActorManager, startState)
                        .transitionOnEvent(eventName).toState(endState).endTransition()
                        .withDefaultEventHandler(defaultTransition);
        assertEquals("State Name", startState, builder.getName());
        assertNotNull("Transition By Name", builder.getTransitionForEvent(eventName));
        final StateDef<StateTableData, StateEvent> actualStateDef = builder.build();
        assertExpectedStateDef(actualStateDef, startState, builder.getTransitions(), defaultTransition);
    }

    @Test
    public void stateBuilder_oneTransitionWithBuiltDefaultTransitionTest() throws Exception {
        final String startState = "TestStartState";
        final String eventName = "TestEvent";
        final String endState = "TestEndState";
        final StateTransitionDef<StateTableData, StateEvent> defaultTransition =
                StateTransitionDefs.getNoActionDefaultTransition();
        final StateDefBuilder<StateTableData, StateEvent> builder =
                new StateDefBuilderImpl<>(null, transitionActorManager, startState)
                        .transitionOnEvent(eventName).toState(endState).endTransition()
                        .withDefaultEventHandler().toState(StateDef.STAY_IN_STATE).endTransition();
        assertEquals("State Name", startState, builder.getName());
        assertNotNull("Transition By Name", builder.getTransitionForEvent(eventName));
        assertNull("Expected No Transition By Name", builder.getTransitionForEvent("Bogus"));
        assertEquals("Default Transition", defaultTransition, builder.getDefaultTransition());
        final StateDef<StateTableData, StateEvent> actualStateDef = builder.build();
        assertExpectedStateDef(actualStateDef, startState, builder.getTransitions(), defaultTransition);
        final StateTransitionDef<StateTableData, StateEvent> transitionForEvent =
                actualStateDef.getTransitionForEvent(eventName);
        assertNotNull("Transition for Event", transitionForEvent);
        assertEquals("Transition To State", endState, transitionForEvent.getTargetStateName());
    }

    @Test(expected=IllegalArgumentException.class)
    public void stateBuilder_constructorNullTransitionManagerTest() throws Exception {
        final String startState = "TestStartState";
        final TransitionActorManager<StateTableData, StateEvent> nullTransitionActorManager = null;
        new StateDefBuilderImpl<>(null, nullTransitionActorManager, startState);
    }

    @Test(expected=IllegalArgumentException.class)
    public void stateBuilder_constructorNullStartStateTest() throws Exception {
        final String nullStartState = null;
        new StateDefBuilderImpl<>(null, transitionActorManager, nullStartState);
    }

    @Test(expected=IllegalArgumentException.class)
    public void stateBuilder_constructorBlankStartStateTest() throws Exception {
        final String blankStartState = " ";
        new StateDefBuilderImpl<>(null, transitionActorManager, blankStartState);
    }

    @Test(expected=IllegalArgumentException.class)
    public void stateBuilder_appendNullTransitionTest() throws Exception {
        final String startState = "TestStartState";
        final StateTransitionDef<StateTableData, StateEvent> nullTransition = null;
        new StateDefBuilderImpl<>(null, transitionActorManager, startState).appendStateTransition(nullTransition);
    }

    @Test(expected=IllegalArgumentException.class)
    public void stateBuilder_withNullDefaultTransitionTest() throws Exception {
        final String startState = "TestStartState";
        final StateTransitionDef<StateTableData, StateEvent> nullTransition = null;
        new StateDefBuilderImpl<>(null, transitionActorManager, startState).withDefaultEventHandler(nullTransition);
    }

    @Test(expected=IllegalArgumentException.class)
    public void stateBuilder_withWrongEventDefaultTransitionTest() throws Exception {
        final String startState = "TestStartState";
        final String eventName = "TestEvent";
        final String endState = "TestEndState";
        final StateTransitionDef<StateTableData, StateEvent> wrongTransition =
                StateTransitionDefs.getNoActionTransition(eventName, endState);
        new StateDefBuilderImpl<>(null, transitionActorManager, startState).withDefaultEventHandler(wrongTransition);
    }

    @Test(expected=IllegalArgumentException.class)
    public void stateBuilder_endStateEmptyTransitionListTest() throws Exception {
        final String startState = "TestStartState";
        final StateTableDefBuilder<StateTableData, StateEvent> stateTableDefBuilder = new StateTableDefBuilderImpl<>();
        stateTableDefBuilder.withState(startState).endState();
    }

    @Test(expected=IllegalStateException.class)
    public void stateBuilder_endStateWithNoParentTest() throws Exception {
        final String startState = "TestStartState";
        final String eventName = "TestEvent";
        final String endState = "TestEndState";
        final StateTransitionDef<StateTableData, StateEvent> noActionTransition =
                StateTransitionDefs.getNoActionTransition(eventName, endState);
        new StateDefBuilderImpl<>(null, transitionActorManager, startState).appendStateTransition(noActionTransition).endState();
    }

    @Test
    public void stateBuilder_endStateTest() throws Exception {
        final String startState = "TestStartState";
        final String eventName = "TestEvent";
        final String endState = "TestEndState";
        final StateTransitionDef<StateTableData, StateEvent> noActionTransition =
                StateTransitionDefs.getNoActionTransition(eventName, endState);
        final StateTableDefBuilder<StateTableData, StateEvent> stateTableDefBuilder = new StateTableDefBuilderImpl<>();
        stateTableDefBuilder.withState(startState).appendStateTransition(noActionTransition).endState();
        final StateDef<StateTableData, StateEvent> actualStateDef = stateTableDefBuilder.getState(startState);
        assertExpectedStateDef(actualStateDef, startState, Collections.singletonList(noActionTransition),
                StateTransitionDefs.getUnexpectedEventDefaultTransition());
    }

    @Test(expected=IllegalArgumentException.class)
    public void stateDef_constructorNullNameTest() throws Exception {
        final String nullName = null;
        final String eventName = "TestEvent";
        final String endState = "TestEndState";
        final StateTransitionDef<StateTableData, StateEvent> noActionTransition =
                StateTransitionDefs.getNoActionTransition(eventName, endState);
        final StateTransitionDef<StateTableData, StateEvent> defaultTransition =
                StateTransitionDefs.getNoActionDefaultTransition();
        new StateDefImpl<>(nullName, Collections.singletonList(noActionTransition), defaultTransition);
    }

    @Test(expected=IllegalArgumentException.class)
    public void stateDef_constructorBlankNameTest() throws Exception {
        final String blankName = "";
        final String eventName = "TestEvent";
        final String endState = "TestEndState";
        final StateTransitionDef<StateTableData, StateEvent> noActionTransition =
                StateTransitionDefs.getNoActionTransition(eventName, endState);
        final StateTransitionDef<StateTableData, StateEvent> defaultTransition =
                StateTransitionDefs.getNoActionDefaultTransition();
        new StateDefImpl<>(blankName, Collections.singletonList(noActionTransition), defaultTransition);
    }

    @Test(expected=IllegalArgumentException.class)
    public void stateDef_constructorNullTransitionsTest() throws Exception {
        final String stateName = "TestStateName";
        final List<StateTransitionDef<StateTableData, StateEvent>> nullTransitions = null;
        final StateTransitionDef<StateTableData, StateEvent> defaultTransition =
                StateTransitionDefs.getNoActionDefaultTransition();
        new StateDefImpl<>(stateName, nullTransitions, defaultTransition);
    }

    @Test(expected=IllegalArgumentException.class)
    public void stateDef_constructorNoTransitionsTest() throws Exception {
        final String stateName = "TestStateName";
        final List<StateTransitionDef<StateTableData, StateEvent>> noTransitions = Collections.emptyList();
        final StateTransitionDef<StateTableData, StateEvent> defaultTransition =
                StateTransitionDefs.getNoActionDefaultTransition();
        new StateDefImpl<>(stateName, noTransitions, defaultTransition);
    }

    private void assertExpectedStateDef(
            final StateDef<StateTableData, StateEvent> actualStateDef,
            final String expectedStateName,
            final Collection<StateTransitionDef<StateTableData, StateEvent>> expectedTransitions,
            final StateTransitionDef<StateTableData, StateEvent> expectedDefaultTransition) {
        assertEquals("State Name", expectedStateName, actualStateDef.getName());
        assertThat(actualStateDef.getTransitions()).containsAll(expectedTransitions);
        assertEquals("Default Transition", expectedDefaultTransition, actualStateDef.getDefaultTransition());
    }
}
