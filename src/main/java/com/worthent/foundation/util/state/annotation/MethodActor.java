/**
 * Copyright 2000-2011 Worth Enterprises, Inc. All rights reserved.
 */
package com.worthent.foundation.util.state.annotation;

import java.lang.reflect.Method;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;
import com.worthent.foundation.util.state.*;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Provides an implementation of the Transition Actor interface from an Actor annotation on a static method or on a
 * data object.
 *
 * @author Erik K. Worth
 * @version $Id: MethodActor.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
 */
public class MethodActor<D extends StateTableData, E extends StateEvent> implements TransitionActor<D, E> {

    public enum ArgumentType {
        /** The actor method has no arguments */
        NONE,
        /** The actor method takes the event as the argument */
        EVENT,
        /** The actor method takes the full transition context as the argument */
        CONTEXT;
    }

    private final ArgumentType argumentType;

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
     * @param argumentType the type of argument the actor method expects
     * @param actorClass the class with the non-static actor method
     * @param actorMethod the annotated actor method
     * @param name the name of the actor
     */
    public MethodActor(
            @NotNull  final ArgumentType argumentType,
            @Nullable final Class<?> actorClass,
            @NotNull  final Method actorMethod,
            @NotNull  final String name) {
        this.argumentType = checkNotNull(argumentType, "argumentType must not be null");
        this.actorClass = actorClass;
        this.actorMethod = actorMethod;
        this.name = name;
    }

    /**
     * Constructs a transition actor able to invoke a static method annotated as
     * a transition actor.
     *
     * @param argumentType the type of argument the actor method expects
     * @param actorMethod the annotated actor method
     * @param name the name of the actor
     */
    public MethodActor(
            @NotNull final ArgumentType argumentType,
            @NotNull final Method actorMethod,
            @NotNull final String name) {
        this(argumentType, null, actorMethod, name);
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
        final Object arg;
        switch (argumentType) {
            case NONE:
                arg = null;
                break;
            case EVENT:
                arg = context.getEvent();
                break;
            case CONTEXT:
                arg = context;
                break;
            default:
                throw new StateExeException("Unknown actor method argument type: " + argumentType);
        }
        try {
            if (ArgumentType.NONE.equals(argumentType)) {
                actorMethod.invoke(data);
            } else {
                actorMethod.invoke(data, arg);
            }
        } catch (final Exception exc) {
            throw new StateExeException("Error invoking method, " + actorMethod.getName(), exc);
        }
    }

}
