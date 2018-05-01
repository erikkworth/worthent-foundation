package com.worthent.foundation.util.metadata;

import java.io.Serializable;
import java.util.Objects;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Holds the field name, type and initial value for one field in a structure type.
 * 
 * @author Erik K. Worth
 */
public class NamedType implements Serializable {

    /** Serial Version ID */
    private static final long serialVersionUID = -1566784915572355080L;

    /** Field name */
    private final String name;

    /** Field type */
    private final DataType type;

    /** Initial Value */
    private final Object initialValue;
    
    /** Used for internalization purposes only */
    @SuppressWarnings("unused")
    private NamedType() {
        name = null;
        type = null;
        initialValue = null;
    }

    /**
     * Construct from elements.
     * 
     * @param name the field name. Must not be <code>null</code>.
     * @param type the field type. Must not be <code>null</code>.
     */
    public NamedType(final String name, final DataType type) {
        this(name, type, null);
    }

    /**
     * Construct from elements.
     * 
     * @param name the field name. Must not be <code>null</code>.
     * @param type the field type. Must not be <code>null</code>.
     * @param initialValue the initial value for the field. May be
     *        <code>null</code>.
     */
    public NamedType(
        @NotNull final String name,
        @NotNull final DataType type,
        @Nullable final Object initialValue) {
        this.name = checkNotNull(name, "name must not be null");
        this.type = checkNotNull(type, "type must not be null");
        this.initialValue = initialValue;
    }

    /**
     * Copy constructor
     *
     * @param other the other type to copy
     */
    public NamedType(@NotNull final NamedType other) {
        checkNotNull(other, "other must not be null");
        this.name = other.name;
        this.type = other.type.deepCopy();
        this.initialValue = other.initialValue;
    }

    /** @return the field name */
    public final String getName() {
        return name;
    }

    /** @return the field type */
    public final DataType getType() {
        return type;
    }

    /** @return the initial value or <code>null</code> if one is not specified */
    public final Object getInitialValue() {
        return initialValue;
    }

    @Override
    public String toString() {
        return "NamedType{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", initialValue=" + initialValue +
                '}';
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NamedType)) {
            return false;
        }
        final NamedType that = (NamedType) other;
        return (name.equals(that.name) &&
            type.equals(that.type) &&
            Objects.equals(initialValue, that.initialValue));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, initialValue);
    }
}
