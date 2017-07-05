/**
 * Copyright 2000-2016 Worth Enterprises, Inc. All rights reserved.
 */
package com.worthent.foundation.util.state.etc.obj;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.worthent.foundation.util.state.etc.obj.ConstructorParameter.*;

/**
 * Annotation placed on a constructor parameter identifying the field.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ObjectField {

    /** @return the name of the field */
    String value();

    /** @return the element type or the default void.class when not a list field */
    Class<?> elementType() default void.class;
}
