/**
 * Copyright 2000-2011 Worth Enterprises, Inc. All rights reserved.
 */
package com.worthent.foundation.util.state.annotation;

import java.lang.reflect.Method;

import com.worthent.foundation.util.state.*;

/**
 * Provides an implementation of the Transition Actor interface from an Actor annotation on a static method or on a
 * data object.
 *
 * @author Erik K. Worth
 * @version $Id: MethodActor.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
 */
public class MethodActor<D extends StateTableData, E extends StateEvent> implements TransitionActor<D, E> {

    /** The class with the non-static actor method */
    private final Class<?> actorClass;

    /** The annotated actor method */
    private final Method actorMethod;

    /** The actor name */
    private final String name;

    /**
     * Constructs a transition actor able to invoke a method annotated as a
     * transition actor.
     * 
     * @param actorClass the class with the non-static actor method
     * @param actorMethod the annotated actor method
     * @param name the name of the actor
     */
    public MethodActor(
        final Class<?> actorClass,
        final Method actorMethod,
        final String name) {
        this.actorClass = actorClass;
        this.actorMethod = actorMethod;
        this.name = name;
    }

    /**
     * Constructs a transition actor able to invoke a static method annotated as
     * a transition actor.
     * 
     * @param actorMethod the annotated actor method
     * @param name the name of the actor
     */
    public MethodActor(final Method actorMethod, final String name) {
        this(null, actorMethod, name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.worthent.foundation.core.util.state.TransitionActor#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void onAction(final TransitionContext<D, E> context) throws StateExeException {
        final Object data = (null == actorClass) ? null : context.getStateTableData();
        try {
            actorMethod.invoke(data, context);
        } catch (final Exception exc) {
            throw new StateExeException("Error invoking method, " + actorMethod.getName(), exc);
        }
    }

}
