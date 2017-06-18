/*
 * Copyright 2000-2016 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.etc.xml;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.StateErrorHandler;
import com.worthent.foundation.util.state.StateExeException;
import com.worthent.foundation.util.state.StateTable;
import com.worthent.foundation.util.state.StateTableControl;
import com.worthent.foundation.util.state.def.StateDef;
import com.worthent.foundation.util.state.def.StateTransitionDefs;
import com.worthent.foundation.util.state.etc.obj.ObjectConstructionEvent;
import com.worthent.foundation.util.state.impl.StateTableBuilderImpl;
import com.worthent.foundation.util.state.provider.SerialStateTableControl;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Adapter that consumes XML Events and produces Object Construction Events.
 *
 * @author Erik K. Worth
 */
public class XmlObjectBuilderAdapter implements StateTableControl<XmlEvent> {

    /**
     * The State Table Control object that feeds XML events to the State table that generates object creation events
     * from XML Events.
     */
    private final StateTableControl<XmlEvent> stateTableControl;

    /** Maintains the temporary state for the XML Object generation state table */
    private final XmlData xmlData;

    /**
     * Construct the XML Object Builder Adapter with the State Table Control that can build an object hierarchy from
     * Object Construction Events.
     *
     * @param stateTableControl the downstream State Table Control that can build an object hierarchy
     */
    public XmlObjectBuilderAdapter(@NotNull final StateTableControl<ObjectConstructionEvent> stateTableControl) {
        this.xmlData = new XmlData(checkNotNull(stateTableControl, "stateTableControl must not be null"));
        final StateErrorHandler<XmlData, XmlEvent> stateErrorHandler = new XmlStateErrorHandler();
        final StateTable<XmlData, XmlEvent> objectConstructionStateTable = new StateTableBuilderImpl<XmlData, XmlEvent>()
                .withStateTableDefinition()
                .setName("XMLObjectBuilderAdapter")
                .usingActorsInClass(XmlData.class)
                .withState(XmlObjectStates.AWAITING_DOCUMENT.name())
                    .transitionOnEvent(SaxEventAdapter.START_DOCUMENT)
                        .toState(XmlObjectStates.AWAITING_OBJECT_ELEMENT_START.name())
                        .withActorsByName(XmlData.PROCESS_DOCUMENT_START)
                        .endTransition()
                    .withDefaultEventHandler(StateTransitionDefs.getUnexpectedEventDefaultTransition())
                    .endState()
                .withState(XmlObjectStates.AWAITING_OBJECT_ELEMENT_START.name())
                    .transitionOnEvent(SaxEventAdapter.START_ELEMENT)
                        .toState(XmlObjectStates.BUILDING_OBJECT.name())
                        .withActorsByName(XmlData.PROCESS_ELEMENT_START, XmlData.SIGNAL_ROOT_START)
                        .endTransition()
                    .transitionOnEvent((SaxEventAdapter.WHITESPACE))
                        .toState(StateDef.STAY_IN_STATE)
                        .withActorsByName(XmlData.PROCESS_WHITESPACE)
                        .endTransition()
                    .withDefaultEventHandler(StateTransitionDefs.getUnexpectedEventDefaultTransition())
                    .endState()
                .withState(XmlObjectStates.BUILDING_OBJECT.name())
                    .transitionOnEvent(SaxEventAdapter.START_ELEMENT)
                        .toState(XmlObjectStates.BUILDING_OBJECT.name())
                        .withActorsByName(XmlData.PROCESS_ELEMENT_START, XmlData.SIGNAL_ENTITY_START)
                        .endTransition()
                    .transitionOnEvent(SaxEventAdapter.CHARACTER_DATA)
                        .toState(XmlObjectStates.BUILDING_FIELD.name())
                        .withActorsByName(XmlData.PROCESS_CHARACTER_DATA)
                        .endTransition()
                    .transitionOnEvent(SaxEventAdapter.WHITESPACE)
                        .toState(StateDef.STAY_IN_STATE)
                        .withActorsByName(XmlData.PROCESS_WHITESPACE)
                        .endTransition()
                    .transitionOnEvent(SaxEventAdapter.END_ELEMENT)
                        .toState(XmlObjectStates.BUILDING_OBJECT.name())
                        .withActorsByName(XmlData.PROCESS_ELEMENT_END, XmlData.SIGNAL_OBJECT_DONE)
                        .endTransition()
                    .transitionOnEvent(SaxEventAdapter.END_DOCUMENT)
                        .toState(XmlObjectStates.AWAITING_DOCUMENT.name())
                        .withActorsByName(XmlData.PROCESS_DOCUMENT_END)
                        .endTransition()
                    .withDefaultEventHandler(StateTransitionDefs.getUnexpectedEventDefaultTransition())
                    .endState()
                .withState(XmlObjectStates.BUILDING_FIELD.name())
                    .transitionOnEvent(SaxEventAdapter.CHARACTER_DATA)
                        .toState(StateDef.STAY_IN_STATE)
                        .withActorsByName(XmlData.PROCESS_CHARACTER_DATA)
                        .endTransition()
                    .transitionOnEvent(SaxEventAdapter.WHITESPACE)
                        .toState(StateDef.STAY_IN_STATE)
                        .withActorsByName(XmlData.PROCESS_WHITESPACE)
                        .endTransition()
                    .transitionOnEvent(SaxEventAdapter.END_ELEMENT)
                        .toState(XmlObjectStates.BUILDING_OBJECT.name())
                        .withActorsByName(XmlData.PROCESS_ELEMENT_END, XmlData.SIGNAL_SIMPLE_VALUE)
                        .endTransition()
                    .withDefaultEventHandler(StateTransitionDefs.getUnexpectedEventDefaultTransition())
                    .endState()
                .endDefinition()
                .withStateTableDataManager().withDataGetter((e) -> xmlData).endDataManager()
                .withErrorHandler(stateErrorHandler)
                .build();
        this.stateTableControl = new SerialStateTableControl<>(objectConstructionStateTable);
    }

    @Override
    public void start() throws StateExeException {
        stateTableControl.start();
    }

    @Override
    public void stop() throws StateExeException {
        stateTableControl.stop();
    }

    @Override
    public void signalEvent(@NotNull final XmlEvent event) throws StateExeException {
        stateTableControl.signalEvent(event);
    }
}
