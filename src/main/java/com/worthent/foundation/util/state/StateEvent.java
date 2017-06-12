/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state;

/**
 * Defines the basic event applied to the state transition table.
 * 
 * @author Erik K. Worth
 */
public interface StateEvent {
    /** Returns the string identifier for this event */
    String getName();
} // StateEvent
