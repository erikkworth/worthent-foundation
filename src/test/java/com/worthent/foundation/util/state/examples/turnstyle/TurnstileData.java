package com.worthent.foundation.util.state.examples.turnstyle;

import com.worthent.foundation.util.state.AbstractStateTableData;
import com.worthent.foundation.util.state.StateEvent;
import com.worthent.foundation.util.state.TransitionContext;
import com.worthent.foundation.util.state.annotation.Actor;

/** The state table data object for the turnstile */
public class TurnstileData extends AbstractStateTableData {

    /** The name of the actor that increments the turn and ticket counts */
    public static final String INCREMENT_COUNT = "incrementCount";

    /** The number of people that passed through the turnstile */
    private int turnCount;
    /** The number of tickets scanned for the turnstile */
    private int ticketCount;

    /** Construct in initial state */
    public TurnstileData() {
        super(TurnstileStates.OFF.name(), TurnstileStates.OFF.name());
        turnCount = 0;
        ticketCount = 0;
    }

    /** Copy constructor */
    public TurnstileData(final TurnstileData other) {
        super(other);
        this.turnCount = other.getTurnCount();
        this.ticketCount = other.getTicketCount();
    }

    /** Set from other state */
    public void set(final TurnstileData other) {
        super.set(other);
        this.turnCount = other.getTurnCount();
        this.ticketCount = other.getTicketCount();
    }

    /** Actor used to increment a counter based on the event type */
    @Actor(name = INCREMENT_COUNT)
    public void increment(final TransitionContext<TurnstileData, StateEvent> context) {
        final String eventName = context.getEvent().getName();
        if (TurnstileEventType.PUSH.name().equals(eventName)) {
            turnCount++;
        } else if (TurnstileEventType.TICKET.name().equals(eventName)) {
            ticketCount++;
        }
    }

    /** Returns the current turnCount */
    public int getTurnCount() {
        return turnCount;
    }

    /** Returns the current ticket count */
    public int getTicketCount() { return ticketCount; }
}
