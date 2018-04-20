/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.examples.turnstyle;

import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateExeException;
import com.worthent.foundation.util.state.StateTableControl;
import com.worthent.foundation.util.state.provider.SerialStateTableControl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

import static com.worthent.foundation.util.state.examples.turnstyle.TurnstileStateTable.OFF_EVENT;
import static com.worthent.foundation.util.state.examples.turnstyle.TurnstileStateTable.ON_EVENT;
import static com.worthent.foundation.util.state.examples.turnstyle.TurnstileStateTable.PUSH_EVENT;
import static com.worthent.foundation.util.state.examples.turnstyle.TurnstileStateTable.TICKET_EVENT;
import static com.worthent.foundation.util.state.examples.turnstyle.TurnstileStateTable.assertExpectedState;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for the builder that defines a state table.
 *
 * @author Erik K. Worth
 */
public class SerialStateTableControlTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerialStateTableControlTest.class);

    /** The list of state transitions through which the state table transitioned during the test */
    private LinkedList<String> stateQueue;

    /** The state table used to test the controller */
    private TurnstileStateTable turnstileStateTable;

    /** The state table controller being tested here */
    private StateTableControl<StateEvent> stateTableController;

    @Rule
    public TestWatcher watchman= new TestWatcher() {
        @Override
        public void starting(final Description description) {
            LOGGER.debug("Starting test {}", description.getMethodName());
        }
    };

    @Before
    public void setup() {
        stateQueue = new LinkedList<>();
        turnstileStateTable = new TurnstileStateTable(stateQueue);
        stateTableController = new SerialStateTableControl<>(turnstileStateTable.getTurnstileStateTable());
    }

    @Test
    public void testOneTurnstileEntry() {
        stateTableController.start();
        stateTableController.signalEvent(ON_EVENT);
        stateTableController.signalEvent(TICKET_EVENT);
        stateTableController.signalEvent(PUSH_EVENT);
        stateTableController.signalEvent(OFF_EVENT);

        assertExpectedState(stateQueue, TurnstileStates.LOCKED);
        assertExpectedState(stateQueue, TurnstileStates.UNLOCKED);
        assertExpectedState(stateQueue, TurnstileStates.LOCKED);
        assertExpectedState(stateQueue, TurnstileStates.OFF);
        assertTrue("Expected empty stateQueue", stateQueue.isEmpty());

        final TurnstileData stateTableData = turnstileStateTable.getStateTableData();
        assertEquals("Expected Turn Count", 1, stateTableData.getTurnCount());
        assertEquals("Expected Ticket Count", 1, stateTableData.getTicketCount());
    }

    @Test
    public void testNoEntryWithoutCoin() {
        stateTableController.start();
        stateTableController.signalEvent(ON_EVENT);
        stateTableController.signalEvent(PUSH_EVENT);
        stateTableController.signalEvent(PUSH_EVENT);

        assertExpectedState(stateQueue, TurnstileStates.LOCKED);
        assertExpectedState(stateQueue, TurnstileStates.LOCKED);
        assertExpectedState(stateQueue, TurnstileStates.LOCKED);
        assertTrue("Expected empty stateQueue", stateQueue.isEmpty());

        final TurnstileData stateTableData = turnstileStateTable.getStateTableData();
        assertEquals("Expected Turn Count", 0, stateTableData.getTurnCount());
        assertEquals("Expected Ticket Count", 0, stateTableData.getTicketCount());

        stateTableController.signalEvent(TICKET_EVENT);
        stateTableController.signalEvent(PUSH_EVENT);
        stateTableController.signalEvent(OFF_EVENT);

        assertExpectedState(stateQueue, TurnstileStates.UNLOCKED);
        assertExpectedState(stateQueue, TurnstileStates.LOCKED);
        assertExpectedState(stateQueue, TurnstileStates.OFF);
        assertTrue("Expected empty stateQueue", stateQueue.isEmpty());

        assertEquals("Expected Turn Count", 1, stateTableData.getTurnCount());
        assertEquals("Expected Ticket Count", 1, stateTableData.getTicketCount());
    }

    @Test(expected=StateExeException.class)
    public void testUnexpectedOnEventWhileAlreadyOn() {
        stateTableController.start();
        stateTableController.signalEvent(ON_EVENT);
        stateTableController.signalEvent(ON_EVENT);
    }

}
