/*
 * Copyright 2000-2016 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.def.impl;

import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateTableData;
import com.worthent.foundation.util.state.TransitionActor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for the State Transition Definition implementation.
 *
 * @author Erik K. Worth
 */
public class StateTransitionDefImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateTransitionDefImplTest.class);

    private static final  String TEST_EVENT = "TestEvent";
    private static final  String GO_NEXT_STATE = "NextState";

    @Rule
    public TestWatcher watchman= new TestWatcher() {
        @Override
        public void starting(final Description description) {
            LOGGER.debug("Starting test {}", description.getMethodName());
        }
    };

    @Mock
    private TransitionActor<StateTableData, StateEvent> actor1;

    @Before
    public void setup() throws Exception {
        initMocks(this);
    }

    @Test(expected=IllegalArgumentException.class)
    public void constructor_WithNullEvent() throws Exception {
        new StateTransitionDefImpl<>(null, GO_NEXT_STATE, Collections.singletonList(actor1));
    }

    @Test(expected=IllegalArgumentException.class)
    public void constructor_WithNullState() throws Exception {
        new StateTransitionDefImpl<>(TEST_EVENT, null, Collections.singletonList(actor1));
    }

    @Test(expected=IllegalArgumentException.class)
    public void constructor_WithNullActorList() throws Exception {
        final List<TransitionActor<StateTableData, StateEvent>> null_actors_list = null;
        new StateTransitionDefImpl<>(TEST_EVENT, GO_NEXT_STATE, null_actors_list);
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void equals_self() throws Exception {
        final StateTransitionDefImpl<StateTableData, StateEvent> stateTransition =
            new StateTransitionDefImpl<>(TEST_EVENT, GO_NEXT_STATE);
        assertTrue("Equals Self", stateTransition.equals(stateTransition));
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Test
    public void equals_NotDifferentType() throws Exception {
        final StateTransitionDefImpl<StateTableData, StateEvent> stateTransition =
                new StateTransitionDefImpl<>(TEST_EVENT, GO_NEXT_STATE, Collections.singletonList(actor1));
        assertFalse("Equals Different type", stateTransition.equals("This is not a state transition"));
    }

    @Test
    public void equals_identicalInstances() throws Exception {
        final StateTransitionDefImpl<StateTableData, StateEvent> stateTransition1 =
                new StateTransitionDefImpl<>(TEST_EVENT, GO_NEXT_STATE, Collections.singletonList(actor1));
        final StateTransitionDefImpl<StateTableData, StateEvent> stateTransition2 =
                new StateTransitionDefImpl<>(TEST_EVENT, GO_NEXT_STATE, actor1);
        assertTrue("Equals Identical", stateTransition1.equals(stateTransition2));
        assertThat(stateTransition1.getActors()).containsOnlyElementsOf(stateTransition2.getActors());
    }

    @Test
    public void equals_identicalButForActors() throws Exception {
        final StateTransitionDefImpl<StateTableData, StateEvent> stateTransition1 =
                new StateTransitionDefImpl<>(TEST_EVENT, GO_NEXT_STATE);
        final StateTransitionDefImpl<StateTableData, StateEvent> stateTransition2 =
                new StateTransitionDefImpl<>(TEST_EVENT, GO_NEXT_STATE, actor1);
        assertTrue("Equals Identical", stateTransition1.equals(stateTransition2));
    }

    @Test
    public void hashCode_sameForIdenticalButForActors() throws Exception {
        final StateTransitionDefImpl<StateTableData, StateEvent> stateTransition1 =
                new StateTransitionDefImpl<>(TEST_EVENT, GO_NEXT_STATE);
        final StateTransitionDefImpl<StateTableData, StateEvent> stateTransition2 =
                new StateTransitionDefImpl<>(TEST_EVENT, GO_NEXT_STATE, actor1);
        assertEquals("Hash Code Identical", stateTransition1.hashCode(), stateTransition2.hashCode());
    }
}
