package com.worthent.foundation.util.state.etc.obj;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.StateExeException;
import com.worthent.foundation.util.state.StateTable;
import com.worthent.foundation.util.state.StateTableControl;
import com.worthent.foundation.util.state.def.StateDef;
import com.worthent.foundation.util.state.def.StateTransitionDefs;
import com.worthent.foundation.util.state.impl.StateTableBuilderImpl;
import com.worthent.foundation.util.state.provider.SerialStateTableControl;

import java.util.function.Consumer;

/**
 * State table controller used to construct objects from Object Construction Events and forward the constructed objects
 * to a consumer.
 *
 * @param <T> The top-level object type being constructed
 */
public class ObjectConstructionController<T> implements StateTableControl<ObjectConstructionEvent> {

    private final StateTableControl<ObjectConstructionEvent> stateTableControl;

    private final ObjectData<T> objectData;

    public ObjectConstructionController(@NotNull final Class<T> objectClass, @NotNull final Consumer<T> resultConsumer) {
        this.objectData = new ObjectData<>(objectClass, resultConsumer);
        final StateTable<ObjectData<T>, ObjectConstructionEvent> stateTable = new StateTableBuilderImpl<ObjectData<T>, ObjectConstructionEvent>()
                .withStateTableDefinition()
                .setName("ObjectBuilder")
                .usingActorsInClass(ObjectData.class)
                .withState(ObjectStates.AWAITING_ROOT_START.name())
                    .transitionOnEvent(ObjectConstructionEvent.EVENT_ROOT_START)
                        .toState(ObjectStates.AWAITING_ENTITY_START.name())
                        .withActorsByName(ObjectData.PROCESS_ROOT_START)
                        .endTransition()
                    .transitionOnEvent(ObjectConstructionEvent.EVENT_DONE)
                        .toState(StateDef.STAY_IN_STATE)
                        .withActorsByName(ObjectData.PROCESS_DONE)
                        .endTransition()
                    .withDefaultEventHandler(StateTransitionDefs.getUnexpectedEventDefaultTransition())
                    .endState()
                .withState(ObjectStates.AWAITING_ENTITY_START.name())
                    .transitionOnEvent(ObjectConstructionEvent.EVENT_ENTITY_START)
                        .toStateConditionallyBeforeEvent(ObjectStates.BUILDING_LIST.name())
                            .when(ObjectData::isBuildingList)
                            .elseGoToState(ObjectStates.BUILDING_ENTITY.name())
                        .withActorsByName(ObjectData.PROCESS_ENTITY_START)
                        .endTransition()
                    .transitionOnEvent(ObjectConstructionEvent.EVENT_OBJECT_DONE)
                        .toStateConditionally(ObjectStates.BUILDING_LIST.name())
                            .when(ObjectData::isBuildingList)
                            .elseStayInState()
                        .withActorsByName(ObjectData.PROCESS_OBJECT_DONE)
                        .endTransition()
                    .transitionOnEvent(ObjectConstructionEvent.EVENT_DONE)
                        .toState(ObjectStates.AWAITING_ROOT_START.name())
                        .withActorsByName(ObjectData.PROCESS_DONE)
                        .endTransition()
                    .withDefaultEventHandler(StateTransitionDefs.getUnexpectedEventDefaultTransition())
                    .endState()
                .withState(ObjectStates.BUILDING_ENTITY.name())
                    .transitionOnEvent(ObjectConstructionEvent.EVENT_ENTITY_START)
                        .toState(ObjectStates.AWAITING_ENTITY_START.name())
                        .withActorsByName(ObjectData.PROCESS_NESTED_ENTITY_START)
                        .endTransition()
                    .transitionOnEvent(ObjectConstructionEvent.EVENT_SIMPLE_VALUE)
                        .toState(ObjectStates.AWAITING_ENTITY_START.name())
                        .withActorsByName(ObjectData.PROCESS_SIMPLE_VALUE)
                        .endTransition()
                    .transitionOnEvent(ObjectConstructionEvent.EVENT_OBJECT_DONE)
                        .toState(ObjectStates.AWAITING_ENTITY_START.name())
                        .withActorsByName(ObjectData.PROCESS_OBJECT_DONE)
                        .endTransition()
                    .withDefaultEventHandler(StateTransitionDefs.getUnexpectedEventDefaultTransition())
                    .endState()
                .withState(ObjectStates.BUILDING_LIST.name())
                    .transitionOnEvent(ObjectConstructionEvent.EVENT_ENTITY_START)
                        .toState(ObjectStates.AWAITING_ENTITY_START.name())
                        .withActorsByName(ObjectData.PROCESS_NESTED_ENTITY_START)
                        .endTransition()
                    .transitionOnEvent(ObjectConstructionEvent.EVENT_OBJECT_DONE)
                        .toState(ObjectStates.AWAITING_ENTITY_START.name())
                        .withActorsByName(ObjectData.PROCESS_OBJECT_DONE)
                        .endTransition()
                    .withDefaultEventHandler(StateTransitionDefs.getUnexpectedEventDefaultTransition())
                    .endState()
                .endDefinition()
                .withStateTableDataManager().withDataGetter(e -> objectData).endDataManager()
                .build();
        this.stateTableControl = new SerialStateTableControl<>(stateTable);
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
    public void signalEvent(@NotNull final ObjectConstructionEvent event) throws StateExeException {
        stateTableControl.signalEvent(event);
    }
}
