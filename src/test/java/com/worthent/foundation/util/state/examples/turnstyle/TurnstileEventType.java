package com.worthent.foundation.util.state.examples.turnstyle;

/** The events applied to the coin-operated turnstile */
public enum TurnstileEventType {
    /** Turn on the turnstile */
    ON,
    /** The user pushes the turnstile arms */
    PUSH,
    /** The user inserts a coin into the turnstile */
    COIN,
    /** Turn off the turnstile */
    OFF
}
