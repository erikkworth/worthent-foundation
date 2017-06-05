package com.worthent.foundation.util.state.etc.obj;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Utility used to build objects.
 */
public class ObjectBuilder extends BaseBuilder {

    /** The object field metadata captured from annotations on each argument to the object constructor */
    private final ConstructionWorker constructionWorker;

    /** The map of field values being built keyed by field name */
    private final Map<String, Object> fields;

    /** Construct from the name of the object being built and the field metadata captured from annotations */
    ObjectBuilder(@Nullable final String name, @NotNull final ConstructionWorker constructionWorker) {
        super(BuilderType.OBJECT_BUILDER, name);
        this.constructionWorker = checkNotNull(constructionWorker, "constructorWorker must not be null");
        this.fields = new HashMap<>();
    }

    @Nullable
    @Override
    public BaseBuilder getFieldBuilder(@NotNull final String name) {
        final String itemName = checkNotNull(name, "name must not be null").toLowerCase();
        final ConstructorParameter parameter = constructionWorker.getConstructorParameter(itemName);
        if (parameter.isCollectionType()) {
            return new ListBuilder(itemName, parameter);
        } else {
            final ConstructionWorker constructionWorker = parameter.getComplexObjectConstructionWorker();
            if (null != constructionWorker) {
                return new ObjectBuilder(itemName, constructionWorker);
            }
        }
        return null;
    }

    @Override
    @NotNull
    public Object build() {
        return constructionWorker.newObject(fields);
    }

    @Override
    @NotNull
    public void set(@NotNull final String name, @Nullable final Object value) {
        checkNotNull(name, "name must not be null");
        if (null != value) {
            fields.put(name.toLowerCase(), value);
        }
    }

}
