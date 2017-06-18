package com.worthent.foundation.util.state.etc.obj;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.AbstractStateTableData;
import com.worthent.foundation.util.state.StateExeException;
import com.worthent.foundation.util.state.annotation.Actor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.function.Consumer;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Data object used to build a data object.
 *
 * @author Erik K. Worth
 */
public class ObjectData<T> extends AbstractStateTableData {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectData.class);

    /** The name of the actor that processes event that initializes the object builder */
    static final String PROCESS_ROOT_START = "processRootStart";

    /** The name of the actor that processes the event indicating the object buildin is complete */
    static final String PROCESS_DONE = "processDone";

    /** The name of the actor that processes the start of a named entity, either an object or field */
    static final String PROCESS_ENTITY_START = "processEntityStart";

    /** The name of the actor that processes the start of the nested named entity */
    static final String PROCESS_NESTED_ENTITY_START = "processNestedEntityStart";

    /** The name of the actor that processes the event that sets a field value on an object */
    static final String PROCESS_SIMPLE_VALUE = "processSimpleValue";

    /** The name of the actor that processes the event that triggers the building of an object from collected fields */
    static final String PROCESS_OBJECT_DONE = "processObjectDone";

    /** The object that consumes the result processed by the state table when a root object is built */
    private final Consumer<T> resultConsumer;

    /** The map of fields for the object being built */
    private final LinkedList<BaseBuilder> objectBuilderStack;

    private final Class<T> objectClass;

    private String itemName;

    ObjectData(@NotNull final Class<T> objectClass, @NotNull final Consumer<T> resultConsumer) {
        super(ObjectStates.AWAITING_ROOT_START.name(), ObjectStates.AWAITING_ROOT_START.name());
        this.objectClass = checkNotNull(objectClass, "objectClass must not be null");
        this.resultConsumer = checkNotNull(resultConsumer, "resultConsumer must not be null");
        this.objectBuilderStack = new LinkedList<>();
        itemName = null;
    }

    @Actor(name = PROCESS_ROOT_START)
    public void processRootStart() {
        objectBuilderStack.clear();
        // Start building the root object
        objectBuilderStack.push(new ObjectBuilder(null, new ConstructionWorker(objectClass)));
    }

    @Actor(name = PROCESS_ENTITY_START)
    public void processEntityStart(final ObjectConstructionEvent event) {
        final String entityName = (String) event.get(ObjectConstructionEvent.PayloadType.ENTITY_NAME);
        LOGGER.debug("Process Entity Start on '{}' for element named, '{}'", this.itemName, entityName);
        final BaseBuilder builder = objectBuilderStack.peek();
        final BaseBuilder nestedBuilder = builder.getFieldBuilder(entityName);
        this.itemName = entityName;
        final BaseBuilder.BuilderType fieldType = (null == nestedBuilder) ? null : nestedBuilder.getType();
        if ((BaseBuilder.BuilderType.LIST_BUILDER.equals(fieldType))) {
            objectBuilderStack.push(nestedBuilder);
        }
    }

    @Actor(name = PROCESS_NESTED_ENTITY_START)
    public void processNestedEntityStart(final ObjectConstructionEvent event) {
        final String entityName = (String) event.get(ObjectConstructionEvent.PayloadType.ENTITY_NAME);
        LOGGER.debug("Process Nested Entity Start on '{}' for element named, '{}'", this.itemName, entityName);
        final BaseBuilder builder = objectBuilderStack.peek();
        final BaseBuilder nestedBuilder = builder.getFieldBuilder(entityName);
        objectBuilderStack.push(nestedBuilder);
        this.itemName = entityName;
    }

    @Actor(name = PROCESS_SIMPLE_VALUE)
    public void processSimpleValue(final ObjectConstructionEvent event) {
        final BaseBuilder objectBuilder = objectBuilderStack.peek();
        final Object fieldValue = event.get(ObjectConstructionEvent.PayloadType.VALUE);
        objectBuilder.set(itemName, fieldValue);
        LOGGER.debug("Set {}.{} with value {}", objectBuilder.getName(), itemName, fieldValue);
        this.itemName = objectBuilder.getName();
    }

    @SuppressWarnings("unchecked cast")
    @Actor(name = PROCESS_OBJECT_DONE)
    public void processObjectDone() {
        BaseBuilder objectBuilder = objectBuilderStack.pop();
        final String objectName = objectBuilder.getName();
        final Object objectValue = objectBuilder.build();
        if (objectBuilderStack.isEmpty()) {
            if (!objectClass.isAssignableFrom(objectValue.getClass())) {
                throw new StateExeException("The built object of type " + objectValue.getClass().getName() +
                        " is not of type " + objectClass.getName());
            }
            resultConsumer.accept((T) objectValue);
        } else {
            objectBuilder = objectBuilderStack.peek();
            objectBuilder.set(objectName, objectValue);
            LOGGER.debug("Set {}.{} with value {}", objectBuilder.getName(), objectName, objectValue);
            this.itemName = objectBuilder.getName();
        }
    }

    /** Returns <code>true</code> when the state table is currently building a list */
    boolean isBuildingList() {
        final BaseBuilder objectBuilder = objectBuilderStack.peek();
        final BaseBuilder.BuilderType fieldType = (null == objectBuilder) ? null : objectBuilder.getType();
        return BaseBuilder.BuilderType.LIST_BUILDER.equals(fieldType);
    }

    @Actor(name = PROCESS_DONE)
    public void processDone() {
        if (!objectBuilderStack.isEmpty()) {
            throw new StateExeException("Processing the Done event but the object builder stack still has this in it: " +
                    objectBuilderStack.peek());
        }
    }
}
