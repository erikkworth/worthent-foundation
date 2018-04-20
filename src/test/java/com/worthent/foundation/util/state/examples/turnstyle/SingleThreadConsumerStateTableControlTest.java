package com.worthent.foundation.util.state.examples.turnstyle;

import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.StateExeException;
import com.worthent.foundation.util.state.provider.SingleThreadConsumerStateTableControl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

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
public class SingleThreadConsumerStateTableControlTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerialStateTableControlTest.class);

    /** The list of state transitions through which the state table transitioned during the test */
    private LinkedBlockingQueue<String> stateQueue;

    /** The state table used to test the controller */
    private TurnstileStateTable turnstileStateTable;

    @Rule
    public TestWatcher watchman= new TestWatcher() {
        @Override
        public void starting(final Description description) {
            LOGGER.debug("Starting test {}", description.getMethodName());
        }
    };

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        stateQueue = new LinkedBlockingQueue<>();
        turnstileStateTable = new TurnstileStateTable(stateQueue);
    }

    @Test
    public void testOneTurnstileEntry() throws Exception {
        try (final SingleThreadConsumerStateTableControl<TurnstileData, StateEvent> stateTableController =
                     new SingleThreadConsumerStateTableControl<>(turnstileStateTable.getTurnstileStateTable())) {
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
    }


    @Test
    public void testOnOffStopAndSignalEvent() throws Exception {
        thrown.expect(StateExeException.class);
        thrown.expectMessage(SingleThreadConsumerStateTableControl.MSG_STATE_TABLE_SHUT_DOWN);

        final SingleThreadConsumerStateTableControl<TurnstileData, StateEvent> stateTableController =
                     new SingleThreadConsumerStateTableControl<>(turnstileStateTable.getTurnstileStateTable());

        stateTableController.start();
        stateTableController.signalEvent(ON_EVENT);

        assertExpectedState(stateQueue, TurnstileStates.LOCKED);
        stateTableController.injectEvent(OFF_EVENT);

        assertExpectedState(stateQueue, TurnstileStates.OFF);
        assertTrue("Expected empty stateQueue", stateQueue.isEmpty());

        // Stop and wait a moment for it to stop
        stateTableController.stop();
        Thread.sleep(200);

        // This should fail
        stateTableController.signalEvent(ON_EVENT);
    }

}
