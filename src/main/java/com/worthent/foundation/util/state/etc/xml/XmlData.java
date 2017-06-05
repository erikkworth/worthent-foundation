package com.worthent.foundation.util.state.etc.xml;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.*;
import com.worthent.foundation.util.state.annotation.Actor;
import com.worthent.foundation.util.state.etc.obj.ObjectConstructionEvent;
import org.xml.sax.Attributes;

import java.util.LinkedList;
import java.util.stream.Collectors;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;


/**
 * State Table data used to track XML Events and generate Object Construction Events to the object construction
 * state table.
 *
 * @author Erik K. Worth
 */
public class XmlData extends AbstractStateTableData {

    /** The name of the actor that processes a type of XML Event */
    public static final String PROCESS_DOCUMENT_START = "processDocumentStart";

    /** The name of the actor that processes a type of XML Event */
    public static final String PROCESS_DOCUMENT_END = "processDocumentEnd";

    /** The name of the actor that processes a type of XML Event */
    public static final String PROCESS_ELEMENT_START = "processElementStart";

    /** The name of the actor that can signal an Object Construction Root Start Event */
    public static final String SIGNAL_ROOT_START = "signalRootStart";

    /** The name of the actor that can signal an Object Construction Entity Start Event */
    public static final String SIGNAL_ENTITY_START = "signalEntityStart";

    /** The name of the actor that processes a type of XML Event */
    public static final String PROCESS_ELEMENT_END = "processElementEnd";

    /** The name of the actor that signals an Object Construction Simple Value Event */
    public static final String SIGNAL_SIMPLE_VALUE = "signalSimpleValue";

    /** The name of the actor that signals an Object Construction Object Value Event */
    public static final String SIGNAL_OBJECT_DONE = "signalValue";

    /** The name of the actor that processes a type of XML Event */
    public static final String PROCESS_CHARACTER_DATA = "processCharacterData";

    /** The name of the actor that processes a type of XML Event */
    public static final String PROCESS_WHITESPACE = "processWhitespace";

    /** Controller for the Object Construction State Table able to create objects from construction events */
    private final StateTableControl<ObjectConstructionEvent> stateTableControl;

    /** The stack of XML elements such that the one on top is the element being processed now */
    private LinkedList<String> elementStack;

    /** The String Builder used to build a string value from character data */
    private StringBuilder fieldValue;

    /** Tracks the line number from the SAX events to report errors */
    private int lineNumber;

    /** Set to true when an XML document is being built */
    private boolean documentStarted;

    /**
     * Construct with the controller to the state table that is able to build an object structure from events.
     */
    public XmlData(@NotNull final StateTableControl<ObjectConstructionEvent> stateTableControl) {
        super(XmlObjectStates.AWAITING_DOCUMENT.name(), XmlObjectStates.AWAITING_DOCUMENT.name());
        this.stateTableControl = checkNotNull(stateTableControl, "stateTableControl must not be null");
        elementStack = new LinkedList<>();
        fieldValue = new StringBuilder();
        lineNumber = 0;
        documentStarted = false;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getElementPath() {
        return elementStack.stream().collect(Collectors.joining("/"));
    }

    @Actor(name = PROCESS_DOCUMENT_START)
    public void processDocumentStart() throws StateExeException {
        if (documentStarted) {
            throw new StateExeException("Received Start Document Event after line " + lineNumber +
                    " when document was already started.");
        }
        documentStarted = true;
    }

    @Actor(name = PROCESS_DOCUMENT_END)
    public void processDocumentEnd() throws StateExeException {
        if (!documentStarted) {
            throw new StateExeException("Received End Document Event when document was not yet started.");
        }
        documentStarted = false;
        if (!elementStack.isEmpty()) {
            throw new StateExeException("Unexpected End of Document at line " + lineNumber +
                    ".  Missing End Elements for element(s): " + elementStack);
        }
        stateTableControl.signalEvent(ObjectConstructionEvent.newDoneEvent());
    }

    @Actor(name = PROCESS_ELEMENT_START)
    public void processElementStart(final StartElementEvent startElementEvent) throws StateExeException {
        if (!documentStarted) {
            throw new StateExeException("Received Start Element Event at line " + lineNumber +
                    " when document was not yet started.");
        }
        final String localName = startElementEvent.getLocalName();
        if (null != localName && localName.length() > 0) {
            elementStack.push(localName);
            return;
        }
        final String qName = startElementEvent.getQualifiedName();
        if (null != qName && qName.length() > 0) {
            elementStack.push(qName);
            return;
        }
        throw new StateExeException("Received Start Element Event at line " + lineNumber + " with a blank element name.");
    }

    @Actor(name = SIGNAL_ROOT_START)
    public void signalRootStart() throws StateExeException {
        stateTableControl.signalEvent(ObjectConstructionEvent.newRootStartEvent());
    }

    @Actor(name = SIGNAL_ENTITY_START)
    public void signalEntityStart(final StartElementEvent startElementEvent) throws StateExeException {
        if (elementStack.isEmpty()) {
            throw new StateExeException("Received Signal Entity Start at line " + lineNumber + " before receiving any elements.");
        }
        stateTableControl.signalEvent(ObjectConstructionEvent.newEntityStartEvent(elementStack.peek()));
        // If the element has attributes, send an event for each
        final Attributes attributes = startElementEvent.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getLocalName(i);
            if (null == name) {
                name = attributes.getQName(i);
            }
            stateTableControl.signalEvent(ObjectConstructionEvent.newEntityStartEvent(name));
            stateTableControl.signalEvent(ObjectConstructionEvent.newSimpleValueEvent(attributes.getValue(i)));
        }
    }

    @Actor(name = PROCESS_ELEMENT_END)
    public void processElementEnd(final EndElementEvent endElementEvent) throws StateExeException {
        if (!documentStarted) {
            throw new StateExeException("Received End Element Event at line " + lineNumber + " when document was not yet started.");
        }
        if (elementStack.isEmpty()) {
            throw new StateExeException("Received End Element Event at line " + lineNumber + " before receiving any elements.");
        }
        final String expectedName = elementStack.peek();
        String actualName = endElementEvent.getLocalName();
        if (null == actualName || actualName.length() == 0) {
            actualName = endElementEvent.getQualifiedName();
        }
        if (!expectedName.equals(actualName)) {
            throw new StateExeException("Received End Element Event at line " + lineNumber + " with name, '" +
                    actualName + "', but expected '" + expectedName + "'");
        }
        elementStack.pop();
    }

    @Actor(name = SIGNAL_SIMPLE_VALUE)
    public void signalSimpleValue() throws StateExeException {
        stateTableControl.signalEvent(ObjectConstructionEvent.newSimpleValueEvent(fieldValue.toString()));
        fieldValue = new StringBuilder();
    }

    @Actor(name = SIGNAL_OBJECT_DONE)
    public void signalObjectDone() throws StateExeException {
        stateTableControl.signalEvent(ObjectConstructionEvent.newObjectDoneEvent());
    }

    @Actor(name = PROCESS_CHARACTER_DATA)
    public void processCharacterData(final XmlEvent event) throws StateExeException {
        final String characters = SaxEventAdapter.assertCharacterData(event);
        trackNewLines(characters);
        fieldValue.append(characters);
    }

    @Actor(name = PROCESS_WHITESPACE)
    public void processWhitespace(final XmlEvent event) throws StateExeException {
        final String characters = SaxEventAdapter.assertCharacterData(event);
        trackNewLines(characters);
    }

    private void trackNewLines(final String characters) {
        final int len = (null == characters) ? 0 : characters.length();
        for (int i = 0; i < len; i++) {
            if ('\n' == characters.charAt(i)) {
                lineNumber++;
            }
        }
    }
}
