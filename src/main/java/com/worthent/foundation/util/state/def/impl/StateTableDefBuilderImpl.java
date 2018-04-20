/*
 * Copyright 2000-2015 Worth Enterprises, Inc.  All rights reserved.
 */
package com.worthent.foundation.util.state.def.impl;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;
import com.worthent.foundation.util.state.*;
import com.worthent.foundation.util.state.annotation.Actor;
import com.worthent.foundation.util.state.annotation.MethodActor;
import com.worthent.foundation.util.state.def.StateDefBuilder;
import com.worthent.foundation.util.state.def.StateDef;
import com.worthent.foundation.util.state.def.StateDefException;
import com.worthent.foundation.util.state.def.StateTableDefBuilder;
import com.worthent.foundation.util.state.def.StateTableDef;
import com.worthent.foundation.util.state.def.StateTransitionDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.worthent.foundation.util.condition.Preconditions.checkNotBlank;
import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Implements the builder that defines a state table.
 *
 * @author Erik K. Worth
 */
public class StateTableDefBuilderImpl<D extends StateTableData, E extends StateEvent>
        extends AbstractChildBuilder<StateTableBuilder<D, E>> implements StateTableDefBuilder<D, E> {

    /** Logger for this class */
    private static final Logger LOGGER = LoggerFactory.getLogger(StateTableDefBuilderImpl.class);

    /** The transition actor manager holding transition actors discovered from class scans for annotations */
    private final TransitionActorManager<D, E> transitionActorManager;

    /** The ID for the state table */
    private String name;

    /** The state definitions being built */
    private final List<StateDef<D, E>> states;

    /** Construct with no parent builder and its own transition actor manager */
    public StateTableDefBuilderImpl() {
        this(null, null);
    }

    /**
     * Construct with the provided parent builder and its own transition actor manager
     *
     * @param parentBuilder the state table builder that created this builder
     */
    public StateTableDefBuilderImpl(@Nullable final StateTableBuilder<D, E> parentBuilder) {
        this(parentBuilder, null);
    }

    /**
     * Construct with a parent builder and shared transition actor manager.
     *
     * @param parentBuilder the builder creating this builder
     * @param transitionActorManager the transition actor manager holding transition actors discovered from class scans
     *                               for annotations
     */
    public StateTableDefBuilderImpl(
            @Nullable final StateTableBuilder<D, E> parentBuilder,
            @Nullable final TransitionActorManager<D, E> transitionActorManager) {
        super(parentBuilder);
        this.transitionActorManager = (null == transitionActorManager)
                ? new TransitionActorManager<>()
                : transitionActorManager;
        states = new LinkedList<>();
    }

    @Override
    @Nullable
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public StateTableDefBuilder<D, E> setName(@Nullable final String name) {
        this.name = name;
        return this;
    }

    /**
     * Adds actors to the state table for each annotated method found in the specified class.
     *
     * @param annotatedClass the class to introspect for Actor annotations
     * @return this builder
     */
    @NotNull
    public StateTableDefBuilder<D, E> usingActorsInClass(@NotNull final Class<?> annotatedClass) {
        final String className = checkNotNull(annotatedClass, "annotatedClass must not be null").getName();
        final Method[] methods = annotatedClass.getMethods();
        for (final Method method : methods) {
            final Actor actor = method.getAnnotation(Actor.class);
            if (null != actor) {
                String name = actor.name();
                if (TransitionActor.UNNAMED.equals(name)) {
                    name = method.getName();
                }
                final MethodActor.ArgumentType argumentType = getMethodArgumentType(className, name, method);
                if (null != argumentType) {
                    final int modifiers = method.getModifiers();
                    if (Modifier.isStatic(modifiers)) {
                        final MethodActor<D, E> methodActor = new MethodActor<>(argumentType, method, name);
                        transitionActorManager.addTransitionActor(methodActor);
                    } else {
                        if (StateTableData.class.isAssignableFrom(annotatedClass)) {
                            final MethodActor<D, E> methodActor =
                                    new MethodActor<>(argumentType, annotatedClass, method, name);
                            transitionActorManager.addTransitionActor(methodActor);
                        } else {
                            LOGGER.warn("Ignoring actor, '{}', in class '{}', because the annotated method is not not static and the class does not extend {}",
                                    name, className, StateTableData.class.getName());
                        }
                    }
                }
            }
        }
        return this;
    }

    @NotNull
    @Override
    public StateDefBuilder<D, E> withState(@NotNull final String stateName) {
        return new StateDefBuilderImpl<>(this, transitionActorManager,
                checkNotBlank(stateName, "stateName must not be blank"));
    }

    @NotNull
    @Override
    public StateDefBuilder<D, E> withState(@NotNull final Enum<?> state) {
        return withState(checkNotNull(state, "state must not be null").name());
    }

    @NotNull
    @Override
    public StateTableDefBuilder<D, E> appendState(@NotNull final StateDef<D, E> state) {
        states.add(checkNotNull(state, "status must not be null"));
        return this;
    }

    @NotNull
    @Override
    public StateDef<D, E> getInitialState() throws StateDefException {
        if (states.isEmpty()) {
            throw new StateDefException("No initial state yet for the state table, '" + name + '"');
        }
        return states.get(0);
    }

    @Override
    public boolean containsState(@NotNull final String stateName) {
        checkNotNull(stateName, "stateName must not be null");
        return states.stream().filter((s) -> stateName.equals(s.getName())).findFirst().isPresent();
    }

    @Override
    @Nullable
    public StateDef<D, E> getState(@NotNull final String stateName) {
        checkNotNull(stateName, "stateName must not be null");
        return states.stream().filter((s) -> stateName.equals(s.getName())).findFirst().orElse(null);
    }

    @NotNull
    @Override
    public StateTransitionDef<D, E> getTransition(
            @NotNull final String stateName,
            @NotNull final String eventName) throws StateExeException {
        checkNotNull(stateName, "stateName must not be null");
        checkNotNull(eventName, "eventName must not be null");
        final Optional<StateDef<D, E>> stateDef = states.stream().filter((s) -> stateName.equals(s.getName())).findFirst();
        if (stateDef.isPresent()) {
            final StateTransitionDef<D, E> transition = stateDef.get().getTransitionForEvent(eventName);
            if (null == transition) {
                throw new StateExeException("No transition found in state, '" + stateName + "' for event, '" + eventName + "'");
            }
            return transition;
        }
        throw new StateExeException("No state in table with name, '" + stateName + "'");
    }

    @NotNull
    @Override
    public StateTableBuilder<D, E> endDefinition() throws StateDefException {
        final StateTableBuilder<D, E> parentBuilder = getParentBuilder();
        final StateTableDef<D, E> stateTableDef = build();
        parentBuilder.withStateTableDefinition(stateTableDef);
        return parentBuilder;
    }

    @NotNull
    @Override
    public StateTableDef<D, E> build() throws StateDefException {
        return new StateTableDefImpl<>(name, states);
    }

    /**
     * Returns the type of argument the actor method expects or <code>null</code> when the method does not have a valid
     * signature.
     *
     * @param className the name of the annotated class
     * @param actorName the name of the method actor
     * @param method the method with the Actor annotation
     * @return <code>true</code> when the annotated method has the appropriate
     *         method signature
     */
    @Nullable
    private static MethodActor.ArgumentType getMethodArgumentType(
            @NotNull final String className,
            @NotNull final String actorName,
            @NotNull final Method method) {
        final int modifiers = method.getModifiers();
        final Class<?>[] types = method.getParameterTypes();
        if (!Modifier.isPublic(modifiers)) {
            LOGGER.warn("Ignoring actor, '{}', in class '{}', because the annotated method is not public.",
                    actorName, className);
            return null;
        }
        if (types.length == 0) {
            return MethodActor.ArgumentType.NONE;
        }
        if (types.length > 1) {
            LOGGER.warn("Ignoring actor, '{}', in class '{}', because the annotated method has more than one parameter.",
                    actorName, className);
            return null;
        }
        if (TransitionContext.class.isAssignableFrom(types[0])) {
            return MethodActor.ArgumentType.CONTEXT;
        }
        if (StateEvent.class.isAssignableFrom(types[0])) {
            return MethodActor.ArgumentType.EVENT;
        }
        // This method has the wrong argument type
        LOGGER.warn(
                "Ignoring actor, '{}', in class '{}', because the first method parameter is not a supported type.",
                actorName, className);
        return null;
    }
}
