package com.worthent.foundation.util.state.examples.turnstyle;

/** The events applied to the turnstile */
public enum TurnstileEventType {
    /** Turn on the turnstile */
    ON,

    /** The user pushes the turnstile arms */
    PUSH,

    /** The attendant scans a ticket */
    TICKET,

    /** Turn off the turnstile */
    OFF
}
