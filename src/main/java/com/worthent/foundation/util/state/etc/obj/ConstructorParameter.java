package com.worthent.foundation.util.state.etc.obj;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;
import com.worthent.foundation.util.state.StateExeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Helper class that represents a constructor parameter for an object to be constructed.
 *
 * @author Erik K. Worth
 */
public class ConstructorParameter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConstructorParameter.class);

    private static final Map<Class<?>, Class<?>> CONCRETE_COLLECTION_CLASSES = new HashMap<Class<?>, Class<?>>() {{
        put(List.class, LinkedList.class);
        put(Set.class, LinkedHashSet.class);
    }};

    private final String name;
    private final Class<?> parameterClass;
    private final Class<?> elementClass;
    private final ConstructionWorker constructionWorker;

    ConstructorParameter(
            @NotNull final String name,
            @NotNull final Class<?> parameterClass,
            @Nullable final Class<?> elementClass) {
        this.name = checkNotNull(name, "name must not be null");
        this.parameterClass = checkNotNull(parameterClass, "parameterClass must not be null");
        this.elementClass = elementClass;
        this.constructionWorker = (null != elementClass) ? scanClass(elementClass) : scanClass(parameterClass);
    }

    public ConstructionWorker getComplexObjectConstructionWorker() {
        return (null == elementClass) && (null != constructionWorker) ? constructionWorker : null;
    }

    public ConstructionWorker getComplexListElementConstructionWorker() {
        return (null != elementClass) && (null != constructionWorker) ? constructionWorker : null;
    }

    public boolean isCollectionType() {
        return null != elementClass;
    }

    @SuppressWarnings("unchecked cast")
    public Collection<Object> newCollection() {
        Class<?> concreteClass = CONCRETE_COLLECTION_CLASSES.get(parameterClass);
        if (null == concreteClass) {
            concreteClass = parameterClass;
        }
        try {
            return (Collection<Object>) concreteClass.newInstance();
        } catch (Exception exc) {
            throw new StateExeException("Error creating instance of parameter class, " + concreteClass.getName(), exc);
        }
    }

    public Object valueOf(final Object value) throws StateExeException {
        if (null == value) {
            throw new StateExeException("Constructor parameter '" + name + "' is null");
        }
        final Class<?> clazz = value.getClass();
        LOGGER.trace("Computing value of parameter '{}' of type {} for object of type {}",
                name, parameterClass.getName(), clazz.getName());
        if (parameterClass.isAssignableFrom(clazz)) {
            return value;
        } else if (value instanceof String) {
            try {
                final Method valueOfMethod = parameterClass.getMethod("valueOf", String.class);
                return valueOfMethod.invoke(null, value);
            } catch (final NoSuchMethodException exc) {
                try {
                    final Constructor<?> stringConstructor = parameterClass.getConstructor(String.class);
                    return stringConstructor.newInstance(value);
                } catch (final NoSuchMethodException e) {
                    throw new StateExeException("Expected constructor parameter '" + name + "' of type, " +
                            parameterClass.getName() + ", to have a valueOf method or a String constructor");
                } catch (final Exception e) {
                    throw new StateExeException("Error converting parameter '" + name + "' of type, " +
                            parameterClass.getName() + ", from the string: " + value);
                }
            } catch (final Exception exc) {
                throw new StateExeException("Error converting parameter '" + name + "' of type, " +
                        parameterClass.getName() + ", from the string: " + value);
            }
        }
        throw new StateExeException("Error obtaining the value of parameter '" + name + "' of type, " +
                parameterClass.getName() + ", for parameter of type: " + parameterClass.getName());
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Class<?> getParameterClass() {
        return parameterClass;
    }

    @Nullable Class<?> getElementClass() {
        return elementClass;
    }

    private static ConstructionWorker scanClass(final Class<?> clazz) {
            final Constructor<?> constructor = ConstructionWorker.findAnnotatedConstructor(clazz);
            if (null != constructor) {
                return new ConstructionWorker(clazz, constructor);
            }
        return null;
    }

}
