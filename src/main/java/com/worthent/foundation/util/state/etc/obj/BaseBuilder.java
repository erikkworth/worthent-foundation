package com.worthent.foundation.util.state.etc.obj;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Abstract base class for complex object or collection builders.
 *
 * @author Erik K. Worth
 */
public abstract class BaseBuilder {

    /** Enumerates the types of builders */
    public enum BuilderType {
        OBJECT_BUILDER,
        LIST_BUILDER;
    }

    /** The name of the entity being built */
    protected final String name;

    /** The type of builder this is */
    private final BuilderType type;

    public BaseBuilder(@NotNull final BuilderType type, @Nullable final String name) {
        this.type = checkNotNull(type, "type must not be null");
        this.name = name;
    }

    /** Returns the name of the entity being built or <code>null</code> when not available */
    @Nullable
    public String getName() {
        return name;
    }

    /** Returns type of builder this is */
    @NotNull
    public BuilderType getType() {
        return type;
    }

    /** Implemented by concrete method to return the builder for the nested field or <code>null</code> when there is
     * no such field or there is no builder for the (probably) primitive field.
     * @param name the name of the nested entity below this one
     * @return returns the builder for the nested field or <code>null</code> when there is
     * no such field or there is no builder for the (probably) primitive field.
     */
    @Nullable
    public abstract BaseBuilder getFieldBuilder(String name);

    /** Construct the complex entity from its fields if any */
    @NotNull
    public abstract Object build();

    /** Sets the value of the field on this object by the field name and object value */
    @NotNull
    public abstract void set(@NotNull String name, @Nullable Object value);
}