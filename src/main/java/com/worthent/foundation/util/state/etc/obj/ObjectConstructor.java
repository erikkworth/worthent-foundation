package com.worthent.foundation.util.state.etc.obj;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation placed on an object constructor identifying the constructor used to create a new data object.
 *
 * @author Erik K. Worth
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface ObjectConstructor {
}
