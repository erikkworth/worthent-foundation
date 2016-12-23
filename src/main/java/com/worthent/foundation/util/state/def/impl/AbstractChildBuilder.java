/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.def.impl;

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

    /** Construct with the parent builder */
    protected AbstractChildBuilder(final P parentBuilder) {
        this.parentBuilder = parentBuilder;
    }

    /** Returns the parent builder or throws an IllegalStateException if there is not parent builder */
    protected P getParentBuilder() {
        if (null == parentBuilder) {
            throw new IllegalStateException("This builder was not created from a parent builder");
        }
        return parentBuilder;
    }
}
