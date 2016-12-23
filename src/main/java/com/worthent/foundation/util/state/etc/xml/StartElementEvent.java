/*
 * Copyright 2000-2016 Worth Enterprises, Inc.  All Rights Reserved.
 */
package com.worthent.foundation.util.state.etc.xml;
import org.xml.sax.Attributes;

import com.worthent.foundation.util.state.StateEvent;

/**
 * Event generated from a SAX Start Element notification
 *
 * @author Erik K. Worth
 */
public class StartElementEvent extends XmlEvent {

    /** Event name for the Start Element notification */
    public static final String EVENT_NAME = SaxEventAdapter.START_ELEMENT;

    /** Element namespace URI */
    private String namespaceURI;

    /** Element local name */
    private String localName;

    /** Element qualified name (prefixed) */
    private String qName;

    /** Element attributes */
    private Attributes attributes;

    @Override
    public final String getName() {
        return EVENT_NAME;
    }

    /**
     * Returns the element namespace URI. The Namespace URI is required when
     * the namespaces property on the parser is set to true (the default),
     * and is optional when the namespaces property is false.
     */
    public final String getNamespaceURI() {
        return namespaceURI;
    }

    /**
     * Returns the element local name. The local name is required when the
     * namespaces property on the parser is set to true (the default), and
     * is optional when the namespaces property is false.
     */
    public final String getLocalName() {
        return localName;
    }

    /**
     * Returns the element qualified name (prefixed). the qualified name is
     * required when the namespace-prefixes property is true, and is
     * optional when the namespace-prefixes property is false (the default).
     */
    public final String getQualifiedName() {
        return qName;
    }

    /** Returns the element attributes */
    public final Attributes getAttributes() {
        return attributes;
    }

    /** Returns the named attribute or <code>null</code> if not found */
    public final String getAttribute(final String attrName) {
        return attributes.getValue(attrName);
    }

    /** Overloads parent method to render more detail on the event */
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(EVENT_NAME);
        buf.append(" <");
        buf.append(qName);
        for (int i = 0; i < attributes.getLength(); i++) {
            buf.append(' ');
            buf.append(attributes.getQName(i));
            buf.append("=\"");
            buf.append(attributes.getValue(i));
            buf.append('"');
        }
        buf.append('>');
        return buf.toString();
    }

    /** This event may only be constructed by the adapter so the constructor is not public */
    StartElementEvent() {
        super(EVENT_NAME);
    }

    void setNamespaceURI(final String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    void setLocalName(final String localName) {
        this.localName = localName;
    }

    void setQName(final String qName) {
        this.qName = qName;
    }

    void setAttributes(final Attributes attributes) {
        this.attributes = attributes;
    }
}
