package com.worthent.foundation.util.state.etc.xml;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.*;
import com.worthent.foundation.util.state.annotation.Actor;
import com.worthent.foundation.util.state.etc.obj.ObjectConstructionEvent;

import java.util.LinkedList;
import java.util.stream.Collectors;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;


/**
 * State Table data used to track XML Events so that the state table can generate Object Construction Events.
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

    /** The name of the actor that processes a type of XML Event */
    public static final String PROCESS_ELEMENT_END = "processElementEnd";

    /** The name of the actor that processes a type of XML Event */
    public static final String PROCESS_CHARACTER_DATA = "processCharacterData";

    /** The name of the actor that processes a type of XML Event */
    public static final String PROCESS_WHITESPACE = "processWhitespace";

    private final StateTableControl<ObjectConstructionEvent> stateTableControl;

    private LinkedList<String> elementStack;

    private int lineNumber;

    private boolean documentStarted;

    public XmlData(@NotNull final StateTableControl<ObjectConstructionEvent> stateTableControl) {
        super(XmlObjectStates.AWAITING_DOCUMENT.name(), XmlObjectStates.AWAITING_DOCUMENT.name());
        this.stateTableControl = checkNotNull(stateTableControl, "stateTableControl must not be null");
        elementStack = new LinkedList<>();
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
    public void processDocumentStart(final TransitionContext<XmlData, XmlEvent> context) throws StateExeException {
        if (documentStarted) {
            throw new StateExeException("Received Start Document Event after line " + lineNumber +
                    " when document was already started.");
        }
        documentStarted = true;
    }

    @Actor(name = PROCESS_DOCUMENT_END)
    public void processDocumentEnd(final TransitionContext<XmlData, XmlEvent> context) throws StateExeException {
        if (!documentStarted) {
            throw new StateExeException("Received End Document Event when document was not yet started.");
        }
        documentStarted = false;
        if (!elementStack.isEmpty()) {
            throw new StateExeException("Unexpected End of Document at line " + lineNumber +
                    ".  Missing End Elements for element(s): " + elementStack);
        }
    }

    @Actor(name = PROCESS_ELEMENT_START)
    public void processElementStart(final TransitionContext<XmlData, XmlEvent> context) throws StateExeException {
        if (!documentStarted) {
            throw new StateExeException("Received Start Element Event at line " + lineNumber +
                    " when document was not yet started.");
        }
        final StartElementEvent startElementEvent = (StartElementEvent) context.getEvent();
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

    @Actor(name = PROCESS_ELEMENT_END)
    public void processElementEnd(final TransitionContext<XmlData, XmlEvent> context) throws StateExeException {
        if (!documentStarted) {
            throw new StateExeException("Received End Element Event at line " + lineNumber + " when document was not yet started.");
        }
        if (elementStack.isEmpty()) {
            throw new StateExeException("Received End Element Event at line " + lineNumber + " before receiving any elements.");
        }
        final EndElementEvent endElementEvent = (EndElementEvent) context.getEvent();
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

    @Actor(name = PROCESS_CHARACTER_DATA)
    public void processCharacterData(final TransitionContext<XmlData, XmlEvent> context) throws StateExeException {
        final String characters = SaxEventAdapter.assertCharacterData(context.getEvent());
        trackNewLines(characters);
    }

    @Actor(name = PROCESS_WHITESPACE)
    public void processWhitespace(final TransitionContext<XmlData, XmlEvent> context) throws StateExeException {
        final String characters = SaxEventAdapter.assertCharacterData(context.getEvent());
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
