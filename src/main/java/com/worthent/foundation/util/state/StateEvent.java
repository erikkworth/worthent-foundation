/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state;

import java.util.EventObject;
import java.util.Map;

/**
 * Defines the basic event applied to the state transition table.
 * 
 * @author Erik K. Worth
 * @version $Id: StateEvent.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
 */
public interface StateEvent {
    /** Returns the string identifier for this event */
    String getName();
} // StateEvent
