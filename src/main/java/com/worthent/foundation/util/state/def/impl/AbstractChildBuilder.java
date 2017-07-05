package com.worthent.foundation.util.state.def.impl;

import com.worthent.foundation.util.annotation.Nullable;

/**
 * A base class for builders that are a child of some parent builder.
 * @param <P> the type of the parent builder
 */
public abstract class AbstractChildBuilder<P> {

    /** The parent builder that created this builder.  It may be null if the builder was created without a parent */
    private final P parentBuilder;

    /** Used when there is no parent builder */
    protected AbstractChildBuilder() {
        this(null);
    }

    /**
     * Construct with the parent builder
     *
     * @param parentBuilder the builder that creates and returns this builder to build a sub-element structure
     */
    protected AbstractChildBuilder(@Nullable final P parentBuilder) {
        this.parentBuilder = parentBuilder;
    }

    /**
     * Returns the parent builder or throws an IllegalStateException if there is no parent builder
     *
     * @return the parent builder
     * @throws IllegalStateException thrown when there is no parent builder
     */
    protected P getParentBuilder() {
        if (null == parentBuilder) {
            throw new IllegalStateException("This builder was not created from a parent builder");
        }
        return parentBuilder;
    }
}
