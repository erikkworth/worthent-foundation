package com.worthent.foundation.util.state.etc.obj;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.state.StateExeException;
import com.worthent.foundation.util.state.def.StateDefException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Helper class used to construct objects from annotations on the constructor and each constructor parameter.
 *
 * @author Erik K. Worth
 */
public class ConstructionWorker {

    /** Use this value to indicate there is no element type and the field is not a collection */
    public static final Class<?> NO_ELEMENT_TYPE = void.class;

    private static final Map<Class<?>, Class<?>> WRAPPED_FOR_PRIMITIVES =
            Stream.of(Boolean.class, Byte.class, Double.class, Float.class, Integer.class, Long.class, Short.class)
            .collect(Collectors.toMap(ConstructionWorker::getPrimitiveClass, Function.identity()));


    private final Class<?> objectClass;

    private final Constructor<?> constructor;

    private final Map<String, ConstructorParameter> constructorParameters;

    ConstructionWorker(@NotNull final Class<?> objectClass) {
        this(objectClass, findAnnotatedConstructor(objectClass));
    }

    ConstructionWorker(@NotNull final Class<?> objectClass, @NotNull final Constructor<?> constructor) {
        this.objectClass = checkNotNull(objectClass, "objectClass must not be null");
        if (null == constructor) {
            throw new StateDefException("Root Object class has no constructor annotated with an ObjectConstructor tag: " +
                    objectClass.getName());
        }
        this.constructor = constructor;
        this.constructorParameters = introspectConstructorSignature(objectClass, constructor);
    }

    ConstructorParameter getConstructorParameter(@NotNull final String name) {
        return constructorParameters.get(name);
    }

    @NotNull
    public Object newObject(final Map<String, Object> fields) throws StateExeException {
        final Object[] parameters = new Object[constructorParameters.size()];
        int i = 0;
        for (final ConstructorParameter constructorParameter : constructorParameters.values()) {
            parameters[i] = constructorParameter.valueOf(fields.get(constructorParameter.getName()));
            i++;
        }
        try {
            return constructor.newInstance(parameters);
        } catch (final Exception exc) {
            throw new StateExeException("Error creating instance of " + objectClass.getName() + " from fields " + fields);
        }
    }

    @NotNull
    static Constructor<?> findAnnotatedConstructor(final Class<?> objectClass) {
        for (final Constructor<?> constructor : objectClass.getConstructors()) {
            final ObjectConstructor objectConstructor = constructor.getAnnotation(ObjectConstructor.class);
            if (null != objectConstructor) {
                return constructor;
            }
        }
        return null;
    }

    private static Map<String, ConstructorParameter> introspectConstructorSignature(
            final Class<?> objectClass,
            final Constructor<?> method) {

        final Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            throw new StateDefException("The annotated constructor for class, " + objectClass.getName() +
                    ", should have at least one parameter");
        }
        final Map<String, ConstructorParameter> constructorParameters = new LinkedHashMap<>();
        for (final Parameter parameter : parameters) {
            final ObjectField objectField = parameter.getAnnotation(ObjectField.class);
            if (null == objectField) {
                throw new StateDefException("The constructor for class, " + objectClass.getName() +
                        ", is missing an ObjectField annotation on parameter, " + parameter.getName());
            }
            String name = objectField.value();
            if (null == name) {
                name = parameter.getName();
            }
            if (null == name) {
                throw new StateExeException("The constructor parameter must have a name: " + objectField);
            }
            name = name.toLowerCase();
            Class<?> parameterClass = parameter.getType();
            if (parameterClass.isPrimitive()) {
                parameterClass = WRAPPED_FOR_PRIMITIVES.get(parameterClass);
            }
            Class<?> elementClass = objectField.elementType();
            if (NO_ELEMENT_TYPE == elementClass) {
                // The default value means it is not a collection type
                elementClass = null;
            }
            constructorParameters.put(name, new ConstructorParameter(name, parameterClass, elementClass));
        }
        return constructorParameters;
    }

    private static Class<?> getPrimitiveClass(final Class<?> wrapperClass) {
        try {
            return (Class<?>) wrapperClass.getField("TYPE").get(null);
        } catch (final Exception exc) {
            throw new RuntimeException("Not a valid primitive wrapper: " + wrapperClass.getName(), exc);
        }
    }
}
