/*
 * Copyright 2000-2016 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state.etc.xml;

/**
 * SAX Event that contains character data that is not purely white space.
 *
 * @author Erik K. Worth
 */
public class CharacterDataEvent extends XmlEvent {

    /** Event name for the Character Data notification */
    public static final String EVENT_NAME = SaxEventAdapter.CHARACTER_DATA;

    /** The character content */
    private String charData;

    @Override
    public String getName() {
        return EVENT_NAME;
    }

    /** @return the character data */
    public final String getCharacterData() {
        return charData;
    }

    /** Overloads parent method to render more detail on the event */
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(EVENT_NAME);
        buf.append(" \"");
        buf.append(charData);
        buf.append('"');
        return buf.toString();
    }

    /** This event may only be constructed by the adapter so the constructor is not public */
    CharacterDataEvent() {
        super(EVENT_NAME);
    }

    /**
     * Set the character data received in the SAX event
     * @param charData the character data received in the SAX event
     */
    void setCharacterData(final String charData) {
        this.charData = charData;
    }

}
