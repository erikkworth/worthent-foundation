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
public class EndElementEvent extends XmlEvent {

    /** Event name for the End Element notification */
    public static final String EVENT_NAME = SaxEventAdapter.END_ELEMENT;

    /** Element namespace URI */
    private String namespaceURI;

    /** Element local name */
    private String localName;

    /** Element qualified name (prefixed) */
    private String qName;

    @Override
    public final String getName() {
        return EVENT_NAME;
    }

    /**
     * Returns the element namespace URI. The Namespace URI is required when
     * the namespaces property on the parser is set to true (the default),
     * and is optional when the namespaces property is false.
     *
     * @return the element namespace URI
     */
    public final String getNamespaceURI() {
        return namespaceURI;
    }

    /**
     * Returns the element local name. The local name is required when the
     * namespaces property on the parser is set to true (the default), and
     * is optional when the namespaces property is false.
     *
     * @return the element local name
     */
    public final String getLocalName() {
        return localName;
    }

    /**
     * Returns the element qualified name (prefixed). the qualified name is
     * required when the namespace-prefixes property is true, and is
     * optional when the namespace-prefixes property is false (the default).
     *
     * @return the element qualified name
     */
    public final String getQualifiedName() {
        return qName;
    }

    /** Overloads parent method to render more detail on the event */
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(EVENT_NAME);
        buf.append(" </");
        buf.append(qName);
        buf.append('>');
        return buf.toString();
    }

    /** This event may only be constructed by the adapter so the constructor is not public */
    EndElementEvent() {
        super(EVENT_NAME);
    }

    /**
     * Sets the namespace URI.
     *
     * @param namespaceURI the namespace URI
     */
    void setNamespaceURI(final String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    /**
     * Sets the local name
     * @param localName the local name
     */
    void setLocalName(final String localName) {
        this.localName = localName;
    }

    /**
     * Sets teh qualified name.
     *
     * @param qName the qualified name
     */
    void setQName(final String qName) {
        this.qName = qName;
    }
}
