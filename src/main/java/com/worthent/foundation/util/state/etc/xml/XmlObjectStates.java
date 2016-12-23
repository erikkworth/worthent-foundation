package com.worthent.foundation.util.state.etc.xml;

/**
 * Enumerates the states through which the XML Object Builder state table transitions as it parses an XML document
 * and emits Object Construction Events to a downstream state table.
 */
public enum XmlObjectStates {
    AWAITING_DOCUMENT,
    AWAITING_OBJECT_ELEMENT_START,
    BUILDING_OBJECT,
    BUILDING_FIELD;
}
