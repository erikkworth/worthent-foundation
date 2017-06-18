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

    static final String EVENT_ROOT_START = "RootStart";

    static final String EVENT_DONE = "Done";

    static final String EVENT_ENTITY_START = "EntityStart";

    static final String EVENT_SIMPLE_VALUE = "SimpleValue";

    static final String EVENT_OBJECT_DONE = "ObjectDone";

    private static final ObjectConstructionEvent ROOT_START_EVENT =
            new ObjectConstructionEvent(EVENT_ROOT_START, PayloadType.NONE, null);

    private static final ObjectConstructionEvent DONE_EVENT =
            new ObjectConstructionEvent(EVENT_DONE, PayloadType.NONE, null);

    private static final ObjectConstructionEvent OBJECT_DONE_EVENT =
            new ObjectConstructionEvent(EVENT_OBJECT_DONE, PayloadType.NONE, null);

    enum PayloadType {
        NONE,
        ENTITY_NAME,
        VALUE;
    }

    public static ObjectConstructionEvent getRootStartEvent() {
        return ROOT_START_EVENT;
    }

    public static ObjectConstructionEvent getDoneEvent() {
        return DONE_EVENT;
    }

    public static ObjectConstructionEvent newEntityStartEvent(@NotNull final String entityName) {
        checkNotNull(entityName, "entityName must not be null");
        return new ObjectConstructionEvent(EVENT_ENTITY_START, PayloadType.ENTITY_NAME, entityName);
    }

    public static ObjectConstructionEvent newSimpleValueEvent(@NotNull final Object simpleValue) {
        checkNotNull(simpleValue, "simpleValue must not be null");
        return new ObjectConstructionEvent(EVENT_SIMPLE_VALUE, PayloadType.VALUE, simpleValue);
    }

    public static ObjectConstructionEvent newObjectDoneEvent() {
        return OBJECT_DONE_EVENT;
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
