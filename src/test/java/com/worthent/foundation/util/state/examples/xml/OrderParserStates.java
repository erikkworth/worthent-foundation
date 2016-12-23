package com.worthent.foundation.util.state.examples.xml;

/**
 * Enumerates the states for parsing a Purchase Order XML document.
 *
 * @author Erik K. Worth
 */
public enum OrderParserStates {
    AWAITING_DOCUMENT,
    AWAITING_PURCHASE_ORDER,
    PROCESSING_PURCHASE_ORDER,
    PROCESSING_LINE_ITEM;
}
