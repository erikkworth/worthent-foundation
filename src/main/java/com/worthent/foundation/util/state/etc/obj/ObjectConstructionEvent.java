package com.worthent.foundation.util.state.etc.obj;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;
import com.worthent.foundation.util.state.StateEvent;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Specifies the events used to build an object hierarchy.
 *
 * @author Erik K. Worth
 */
public class ObjectConstructionEvent implements StateEvent {

    public static final String EVENT_ROOT_START = "RootStart";

    public static final String EVENT_DONE = "Done";

    public static final String EVENT_ENTITY_START = "EntityStart";

    public static final String EVENT_OBJECT_START = "ObjectStart";

    public static final String EVENT_LIST_START = "ListStart";

    public static final String EVENT_SIMPLE_VALUE = "SimpleValue";

    public static final String EVENT_OBJECT_DONE = "ObjectDone";

    enum PayloadType {
        NONE,
        ENTITY_NAME,
        VALUE;
    }

    public static ObjectConstructionEvent newRootStartEvent() {
        return new ObjectConstructionEvent(EVENT_ROOT_START, PayloadType.NONE, null);
    }

    public static ObjectConstructionEvent newDoneEvent() {
        return new ObjectConstructionEvent(EVENT_DONE, PayloadType.NONE, null);
    }

    public static ObjectConstructionEvent newEntityStartEvent(@NotNull final String entityName) {
        checkNotNull(entityName, "entityName must not be null");
        return new ObjectConstructionEvent(EVENT_ENTITY_START, PayloadType.ENTITY_NAME, entityName);
    }

    public static ObjectConstructionEvent newObjectStartEvent(@NotNull final String entityName) {
        checkNotNull(entityName, "entityName must not be null");
        return new ObjectConstructionEvent(EVENT_OBJECT_START, PayloadType.ENTITY_NAME, entityName);
    }

    public static ObjectConstructionEvent newListStartEvent(@NotNull final String entityName) {
        checkNotNull(entityName, "entityName must not be null");
        return new ObjectConstructionEvent(EVENT_LIST_START, PayloadType.ENTITY_NAME, entityName);
    }

    public static ObjectConstructionEvent newSimpleValueEvent(@NotNull final Object simpleValue) {
        checkNotNull(simpleValue, "simpleValue must not be null");
        return new ObjectConstructionEvent(EVENT_SIMPLE_VALUE, PayloadType.VALUE, simpleValue);
    }

    public static ObjectConstructionEvent newObjectDoneEvent() {
        return new ObjectConstructionEvent(EVENT_OBJECT_DONE, PayloadType.NONE, null);
    }

    /** The type of event */
    private final String eventName;

    /** The type of payload the event is carrying */
    private final PayloadType payloadType;

    /** The event payload */
    private final Object payload;

    private ObjectConstructionEvent(
            @NotNull final String eventName,
            @NotNull final PayloadType payloadType,
            @Nullable final Object payload) {
        this.eventName = checkNotNull(eventName, "eventName must not be null");
        this.payloadType = checkNotNull(payloadType, "payloadType must not be null");
        if (!PayloadType.NONE.equals(payloadType)) {
            checkNotNull(payload, "payload must not be null");
        }
        this.payload = payload;
    }

    @Override
    public String getName() {
        return eventName;
    }

    @Override
    public String toString() {
        return (null == payload) ? getName() : getName() + " \"" + payload + "\"";
    }

    /** Returns the value cast to the expected type */
    public Object get(@NotNull final PayloadType payloadType) {
        if (!this.payloadType.equals(payloadType)) {
            throw new IllegalStateException("The caller requested a payload type of " + payloadType +
                    " but the event of type " + eventName + " is carrying a payload of type " + this.payloadType);
        }
        return payload;
    }
}
