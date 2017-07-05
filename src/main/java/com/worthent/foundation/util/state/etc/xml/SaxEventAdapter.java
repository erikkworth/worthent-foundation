/*
 * Copyright 2000-2011 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state.etc.xml;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.StateExeException;
import com.worthent.foundation.util.state.StateTable;
import com.worthent.foundation.util.state.StateTableControl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Provides an implementation of a SAX Content Handler that may be used to feed SAX Events to a State Table.  This
 * adapter is designed to minimize object creations and thus can only be used by a state stable that uses the same
 * thread to consume and act on events.
 *
 * @see StateTable
 * @author Erik K. Worth
 */
public class SaxEventAdapter extends DefaultHandler {

    /** Event name for the Start Document notification */
    static final String START_DOCUMENT = "StartDocument";

    /** Event name for the End Document notification */
    static final String END_DOCUMENT = "EndDocument";

    /** Event name for the Start Element notification */
    static final String START_ELEMENT = "StartElement";

    /** Event name for the End Element notification */
    static final String END_ELEMENT = "EndElement";

    /** Event name for the character data notification */
    static final String CHARACTER_DATA = "CharacterData";

    /** Event name for the whitespace notification */
    static final String WHITESPACE = "Whitespace";

    /** SAX Start Document event (stateless) */
    private static final XmlEvent START_DOCUMENT_EVENT = new XmlEvent(START_DOCUMENT);

    /** SAX End Document event (stateless) */
    private static final XmlEvent END_DOCUMENT_EVENT = new XmlEvent(END_DOCUMENT);

    /** SAX Start Element event (reusable) */
    private final StartElementEvent startElementEvent = new StartElementEvent();

    /** SAX End Element event */
    private final EndElementEvent endElementEvent = new EndElementEvent();

    /** SAX Character Data Event */
    private final CharacterDataEvent characterDataEvent = new CharacterDataEvent();

    /** Whitespace Event */
    private final WhitespaceEvent whitespaceEvent = new WhitespaceEvent();

    /** State table able to consume the state XML Events */
    private final StateTableControl<XmlEvent> stateTableControl;

    /**
     * Helper method that asserts the specified event is a character data event
     * or whitespace and returns the trimmed character data. In the case of
     * whitespace,it returns an empty string.
     */
    static String assertCharacterData(final XmlEvent event)
            throws StateExeException {
        if (event instanceof CharacterDataEvent) {
            final CharacterDataEvent charDataEvent = (CharacterDataEvent) event;
            return charDataEvent.getCharacterData();
        } else if (event instanceof WhitespaceEvent) {
            final WhitespaceEvent whitespaceEvent = (WhitespaceEvent) event;
            return whitespaceEvent.getCharacterData();
        } else {
            throw new StateExeException("Expected event, " +
                    CharacterDataEvent.class.getName() +
                    ", but found instead event, " +
                    event.getClass().getName());
        }
    }

    /**
     * Construct with the state table control for the state table able to consume the SAX events
     *
     * @param stateTableControl the state table control for the state table able to consume XML Events
     */
    public SaxEventAdapter(@NotNull final StateTableControl<XmlEvent> stateTableControl) {
        this.stateTableControl = checkNotNull(stateTableControl, "stateTableControl must not be null");
    }

    /**
     * Receive notification of the beginning of a document. The SAX parser will
     * invoke this method only once, before any other methods.
     */
    public void startDocument() throws SAXException {
        try {
            stateTableControl.signalEvent(START_DOCUMENT_EVENT);
        } catch (final Exception exc) {
            throw new SAXException("State table exception", exc);
        }
    }

    /**
     * Receive notification of the end of a document. The SAX parser will invoke
     * this method only once, and it will be the last method invoked during the
     * parse. The parser shall not invoke this method until it has either
     * abandoned parsing (because of an unrecoverable error) or reached the end
     * of input.
     */
    public void endDocument() throws SAXException {
        try {
            stateTableControl.signalEvent(END_DOCUMENT_EVENT);
        } catch (final Exception exc) {
            throw new SAXException("State table exception", exc);
        }
    }

    /**
     * Receive notification of the beginning of an element. The Parser will
     * invoke this method at the beginning of every element in the XML document;
     * there will be a corresponding endElement event for every startElement
     * event (even when the element is empty). All of the element's content will
     * be reported, in order, before the corresponding endElement event.
     * <p>
     * This event allows up to three name components for each element:
     * <ul>
     * <li>the Namespace URI
     * <li>the local name
     * <li>the qualified (prefixed) name
     * </ul>
     * Any or all of these may be provided, depending on the values of the
     * http://xml.org/sax/features/namespaces and the
     * http://xml.org/sax/features/namespace-prefixes properties:<br>
     * the Namespace URI and local name are required when the namespaces
     * property is true (the default), and are optional when the namespaces
     * property is false (if one is specified, both must be); the qualified name
     * is required when the namespace-prefixes property is true, and is optional
     * when the namespace-prefixes property is false (the default).
     *
     * @param namespaceURI the element namespace URI
     * @param localName the element local name
     * @param qName the element qualified (prefixed) name
     * @param attributes attributes on the element
     */
    public void startElement(
            final String namespaceURI,
            final String localName,
            final String qName,
            final Attributes attributes) throws SAXException {

        // Set the event state
        startElementEvent.setNamespaceURI(namespaceURI);
        startElementEvent.setLocalName(localName);
        startElementEvent.setQName(qName);
        startElementEvent.setAttributes(attributes);

        // Signal the event to the state table
        try {
            stateTableControl.signalEvent(startElementEvent);
        } catch (final Exception exc) {
            throw new SAXException("State table exception", exc);
        }
    }

    /**
     * Receive notification of the end of an element. The SAX parser will invoke
     * this method at the end of every element in the XML document; there will
     * be a corresponding startElement event for every endElement event (even
     * when the element is empty).
     *
     * @param namespaceURI the element namespace URI
     * @param localName the element local name
     * @param qName the element qualified (prefixed) name
     */
    public void endElement(
            final String namespaceURI,
            final String localName,
            final String qName) throws SAXException {
        // Set the event state
        endElementEvent.setNamespaceURI(namespaceURI);
        endElementEvent.setLocalName(localName);
        endElementEvent.setQName(qName);

        // Signal the event to the state table
        try {
            stateTableControl.signalEvent(endElementEvent);
        } catch (final Exception exc) {
            throw new SAXException("State table exception", exc);
        }
    }

    /**
     * Receive notification of character data. The Parser will call this method
     * to report each chunk of character data. SAX parsers may return all
     * contiguous character data in a single chunk, or they may split it into
     * several chunks; however, all of the characters in any single event must
     * come from the same external entity so that the Locator provides useful
     * information.
     */
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        final String chars = new String(ch, start, length);
        try {
            if (allWhitespace(chars)) {

                // Signal this event if all the characters are whitespace
                whitespaceEvent.setCharacterData(chars);
                stateTableControl.signalEvent(whitespaceEvent);
            } else {

                // Signal this event if at least some characters are not whitespace
                characterDataEvent.setCharacterData(chars);
                stateTableControl.signalEvent(characterDataEvent);
            }
        } catch (final Exception exc) {
            throw new SAXException("State table exception", exc);
        }
    }

    /**
     * Receive notification of a recoverable error.
     * <p>
     * This corresponds to the definition of "error" in section 1.2 of the W3C
     * XML 1.0 Recommendation. For example, a validating parser would use this
     * callback to report the violation of a validity constraint. This method
     * throws the received exception.
     */
    public void error(final SAXParseException exception) throws SAXException {
        throw exception;
    }

    /**
     * Receive notification of a non-recoverable error.
     * <p>
     * This corresponds to the definition of "fatal error" in section 1.2 of the
     * W3C XML 1.0 Recommendation. For example, a parser would use this callback
     * to report the violation of a well-formedness constraint. This method
     * throws the received exception.
     */
    public void fatalError(final SAXParseException exception)
            throws SAXException {
        throw exception;
    }

    /** Returns <code>true</code> when all of the characters are whitespace characters */
    private static boolean allWhitespace(final String chars) {
        for (int i = 0; i < chars.length(); i++) {
            if (!Character.isWhitespace(chars.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
