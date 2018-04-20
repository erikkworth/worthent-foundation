/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.def.impl;

import com.google.common.collect.ImmutableList;
import com.worthent.foundation.util.state.*;
import com.worthent.foundation.util.state.def.StateDef;
import com.worthent.foundation.util.state.def.StateTransitionDef;
import com.worthent.foundation.util.state.def.StateTransitionDefs;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test suite for state transition definitions.
 *
 * @author Erik K. Worth
 */
public class StateTransitionDefBuilderImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateTransitionDefBuilderImplTest.class);

    @Rule
    public TestWatcher watchman= new TestWatcher() {
        @Override
        public void starting(final Description description) {
            LOGGER.debug("Starting test {}", description.getMethodName());
        }
    };

    @Mock
    private TransitionActor<StateTableData, StateEvent> actor1;

    @Mock
    private TransitionActor<StateTableData, StateEvent> actor2;

    @Mock
    private TransitionContext<StateTableData, StateEvent> transitionContext;

    @Mock
    private StateTable<StateTableData, StateEvent> stateTable;

    @Captor
    private ArgumentCaptor<TransitionContext<StateTableData, StateEvent>> transitionContextCaptor;

    /** Container for transition actors used during the tests */
    private TransitionActorManager<StateTableData, StateEvent> transitionActorManager;

    @Before
    public void setup() {
        initMocks(this);
        transitionActorManager = new TransitionActorManager<>();
    }

    @Test(expected=IllegalArgumentException.class)
    public void transitionBuilder_constructorWithNullTransitionManagerTest() {
        final TransitionActorManager<StateTableData, StateEvent> noTransitionActorManager = null;
        new StateTransitionDefBuilderImpl<>(null, noTransitionActorManager, "testEvent");
    }

    @Test(expected=IllegalArgumentException.class)
    public void transitionBuilder_constructorWithNullEventTest() {
        final String noEventName = null;
        new StateTransitionDefBuilderImpl<>(null, transitionActorManager, noEventName);
    }

    @Test(expected=IllegalArgumentException.class)
    public void transitionBuilder_constructorWithBlankEventTest() {
        final String blankEventName = "";
        new StateTransitionDefBuilderImpl<>(null, transitionActorManager, blankEventName);
    }

    @Test(expected=IllegalArgumentException.class)
    public void transitionBuilder_toStateWithNullStateTest() {
        final String onEvent = "testEvent";
        final String nullState = null;
        new StateTransitionDefBuilderImpl<>(null, transitionActorManager, onEvent).toState(nullState);
    }

    @Test(expected=IllegalArgumentException.class)
    public void transitionBuilder_toStateWithBlankStateTest() {
        final String onEvent = "testEvent";
        final String blankState = " ";
        new StateTransitionDefBuilderImpl<>(null, transitionActorManager, onEvent).toState(blankState);
    }

    @Test(expected=IllegalArgumentException.class)
    public void transitionBuilder_withActorWithNullActorTest() {
        final String onEvent = "testEvent";
        final String toState = "targetState";
        final TransitionActor<StateTableData, StateEvent> nullActor = null;
        new StateTransitionDefBuilderImpl<>(null, transitionActorManager, onEvent).toState(toState).withActor(nullActor);
    }

    @Test(expected=IllegalArgumentException.class)
    public void transitionBuilder_withActorsByNameWithNullActorNamesTest() {
        final String onEvent = "testEvent";
        final String toState = "targetState";
        final String[] nullActorNames = null;
        new StateTransitionDefBuilderImpl<>(null, transitionActorManager, onEvent).toState(toState).withActorsByName(nullActorNames);
    }

    @Test
    public void transitionBuilder_withActorsByNameWithNoActorNamesTest() {
        final String onEvent = "testEvent";
        final String toState = "targetState";
        new StateTransitionDefBuilderImpl<>(null, transitionActorManager, onEvent).toState(toState).withActorsByName();
    }

    @Test(expected=IllegalStateException.class)
    public void transitionBuilder_withActorsByNameWithMissingActorTest() {
        final String onEvent = "testEvent";
        final String toState = "targetState";
        final String noSuchActor = "bogusActor";
        new StateTransitionDefBuilderImpl<>(null, transitionActorManager, onEvent).toState(toState).withActorsByName(noSuchActor);
    }

    @Test(expected=IllegalStateException.class)
    public void transitionBuilder_endTransitionWithNoParentBuilderTest() {
        final String onEvent = "testEvent";
        final String toState = "targetState";
        new StateTransitionDefBuilderImpl<>(null, transitionActorManager, onEvent).toState(toState).endTransition();
    }

    @Test(expected=IllegalStateException.class)
    public void transitionBuilder_buildWithNoTargetStateTest() {
        final String onEvent = "testEvent";
        new StateTransitionDefBuilderImpl<>(null, transitionActorManager, onEvent).build();
    }

    @Test
    public void transitionBuilder_oneActorInstanceTest() {
        final String onEvent = "testEvent";
        final String toState = "targetState";
        final TransitionActor<StateTableData, StateEvent> actor = context -> LOGGER.debug("Acted");
        final StateTransitionDefBuilderImpl<StateTableData, StateEvent> transitionBuilder =
                new StateTransitionDefBuilderImpl<>(null, transitionActorManager, onEvent);
        final StateTransitionDef<StateTableData, StateEvent> transition = transitionBuilder
                .toState(toState)
                .withActor(actor)
                .build();
        assertEquals("Event Name", onEvent, transitionBuilder.getEventName());
        assertEquals("Target State Name", toState, transitionBuilder.getTargetStateName());
        assertEquals("Transition Actors", Collections.singletonList(actor), transitionBuilder.getActors());
        assertExpectedTransition(transition, onEvent, toState, Collections.singletonList(actor));
    }

    @Test
    public void transitionBuilder_oneNamedActorTest() {
        final String actorName = "testActor";
        final String onEvent = "testEvent";
        final String toState = "targetState";
        when(actor1.getName()).thenReturn(actorName);
        transitionActorManager.addTransitionActor(actor1);
        final StateTransitionDefBuilderImpl<StateTableData, StateEvent> transitionBuilder =
                new StateTransitionDefBuilderImpl<>(null, transitionActorManager, onEvent);
        final StateTransitionDef<StateTableData, StateEvent> transition = transitionBuilder
                .toState(toState)
                .withActorsByName(actorName)
                .build();
        assertExpectedTransition(transition, onEvent, toState, Collections.singletonList(actor1));
        verify(actor1).onAction(transitionContextCaptor.capture());
        assertEquals("Transition Context", transitionContext, transitionContextCaptor.getValue());
    }

    @Test
    public void transitionBuilder_twoNamedActorsTest() {
        final String actorName1 = "testActor1";
        final String actorName2 = "testActor2";
        final String onEvent = "testEvent";
        final String toState = "targetState";
        when(actor1.getName()).thenReturn(actorName1);
        when(actor2.getName()).thenReturn(actorName2);
        transitionActorManager.addTransitionActor(actor1);
        transitionActorManager.addTransitionActor(actor2);
        final StateTransitionDefBuilderImpl<StateTableData, StateEvent> transitionBuilder =
                new StateTransitionDefBuilderImpl<>(null, transitionActorManager, onEvent);
        final StateTransitionDef<StateTableData, StateEvent> transition = transitionBuilder
                .toState(toState)
                .withActorsByName(actorName1, actorName2)
                .build();
        assertExpectedTransition(
                transition,
                onEvent,
                toState,
                new ImmutableList.Builder<TransitionActor<StateTableData, StateEvent>>()
                        .add(actor1)
                        .add(actor2)
                        .build());
        verify(actor1).onAction(transitionContextCaptor.capture());
        assertEquals("Transition Context", transitionContext, transitionContextCaptor.getValue());
        verify(actor2).onAction(transitionContextCaptor.capture());
        assertEquals("Transition Context", transitionContext, transitionContextCaptor.getValue());
    }

    @Test
    public void transitionBuilder_withConditionalTargetStateTest() {
        final String actorName = "testActor";
        final String onEvent = "testEvent";
        final String toState1 = "targetState1";
        final String toState2 = "targetState2";
        when(actor1.getName()).thenReturn(actorName);
        transitionActorManager.addTransitionActor(actor1);
        final StateTransitionDefBuilderImpl<StateTableData, StateEvent> transitionBuilder =
                new StateTransitionDefBuilderImpl<>(null, transitionActorManager, onEvent);
        final StateTransitionDef<StateTableData, StateEvent> transition = transitionBuilder
                .toStateConditionally(toState1).when(test -> true).elseGoToState(toState2)
                .withActorsByName(actorName)
                .build();
        assertExpectedTransition(transition, onEvent, StateDef.STATE_CHANGE_BY_ACTOR, Collections.singletonList(actor1));
        verify(actor1).onAction(transitionContextCaptor.capture());
        assertEquals("Transition Context", transitionContext, transitionContextCaptor.getValue());
        assertThat(transition.getPotentialTargetStateNames()).containsOnly(toState1, toState2);
    }

    @Test(expected=StateExeException.class)
    public void stateTransitionDefs_getUnexpectedEventDefaultTransition() {
        final String onEvent = StateTransitionDef.DEFAULT_HANDLER_EVENT_ID;
        final String toState = StateDef.STAY_IN_STATE;
        final StateEvent mockEvent = mock(StateEvent.class);
        when(mockEvent.getName()).thenReturn("TestEvent");
        when(stateTable.getStateTableName()).thenReturn("MockStateTable");
        when(transitionContext.getEvent()).thenReturn(mockEvent);
        when(transitionContext.getStateTable()).thenReturn(stateTable);
        when(transitionContext.getFromState()).thenReturn("TestFromState");
        final StateTransitionDef<StateTableData, StateEvent> transition =
                StateTransitionDefs.getUnexpectedEventDefaultTransition();
        assertExpectedTransition(transition, onEvent, toState,
                Collections.singletonList(TransitionActors.unexpectedEventActor()));
    }

    @Test
    public void stateTransitionDefs_getNoActionTransition() {
        final String onEvent = "testEvent";
        final String toState = "targetState";
        final StateTransitionDef<StateTableData, StateEvent> transition =
                StateTransitionDefs.getNoActionTransition(onEvent, toState);
        assertExpectedTransition(transition, onEvent, toState, Collections.emptyList());
    }

    /**
     * Verifies an actual transition against expected information.
     *
     * @param transition the actual transition created by the builder
     * @param onEvent the expected event
     * @param toState the expected target state
     * @param actors the expected list of actors
     */
    private void assertExpectedTransition(
            final StateTransitionDef<StateTableData, StateEvent> transition,
            final String onEvent,
            final String toState,
            final List<TransitionActor<StateTableData, StateEvent>> actors) {
        assertEquals("onEvent", onEvent, transition.getEventName());
        assertEquals("toState", toState, transition.getTargetStateName());
        final Iterator<TransitionActor<StateTableData, StateEvent>> actualActors = transition.getActors().iterator();
        for (final TransitionActor<StateTableData, StateEvent> expectedActor : actors) {
            assertTrue("Missing actor " + expectedActor.getName(), actualActors.hasNext());
            final TransitionActor<StateTableData, StateEvent> actualActor = actualActors.next();
            assertEquals("Actor Name", expectedActor.getName(), actualActor.getName());
            actualActor.onAction(transitionContext);
        }
    }
}
