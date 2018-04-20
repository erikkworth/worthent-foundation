package com.worthent.foundation.util.state.impl;

import com.worthent.foundation.util.annotation.NotNull;
import org.junit.Test;

import java.util.Map;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the StateEventBuilder and the StateEventWithDataMap implementations to make sure the classes can be extended
 * and work properly.
 *
 * @author Erik K. Worth
 */
public class StateEventWithDataMapImplTest {

    private static final String TEST_SESSION_ID = "test-session-id";
    private static final String TEST_EVENT_PARAM_VALUE = "test-event-param-value";

    /** Extend the basic StateEventWithDataMap with a required field */
    private final class TestStateEvent extends StateEventWithDataMapImpl {

        private static final String EVENT_1 = "event-1";
        private static final String EVENT_2 = "event-2";

        private static final String PARAM_KEY_1 = "param-key-1";

        /** Every event must have this field */
        private final String sessionId;

        private TestStateEvent(
                @NotNull final String eventName,
                @NotNull final String sessionId,
                @NotNull final Map<String, Object> eventData) {
            super(eventName, eventData);
            this.sessionId = checkNotNull(sessionId, "sessionId must not be null");
        }

        @NotNull
        private String getSessionId() {
            return sessionId;
        }
    }

    /** Extend the basic StateEventBuilder to build a TestStateEvent event */
    private final class TestStateEventBuilder extends StateEventBuilderImpl<TestStateEvent> {

        /** Every event must have this field */
        private final String sessionId;

        /**
         * Construct with the event name
         *
         * @param eventName the identifier for the event
         */
        public TestStateEventBuilder(@NotNull final String eventName, @NotNull final String sessionId) {
            super(eventName);
            this.sessionId = checkNotNull(sessionId, "sessionId must not be null");
        }

        @Override
        @NotNull
        public TestStateEvent build() {
            return new TestStateEvent(getName(), sessionId, getEventData());
        }
    }

    @Test
    public void build_withValidParametersAndConfirm() {
        final TestStateEvent event = new TestStateEventBuilder(TestStateEvent.EVENT_1, TEST_SESSION_ID)
                .withEventData(TestStateEvent.PARAM_KEY_1, TEST_EVENT_PARAM_VALUE)
                .build();
        assertThat(event.getName()).isEqualTo(TestStateEvent.EVENT_1);
        assertThat(event.getSessionId()).isEqualTo(TEST_SESSION_ID);
        final String param1 = event.getRequiredEventData(TestStateEvent.PARAM_KEY_1);
        assertThat(param1).isEqualTo(TEST_EVENT_PARAM_VALUE);
    }
}
