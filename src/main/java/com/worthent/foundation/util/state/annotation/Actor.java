package com.worthent.foundation.util.state.annotation;

import com.worthent.foundation.util.state.TransitionActor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to put on methods that serve as state transition actors.
 * 
 * @author Erik K. Worth
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Actor {
    /** @return the name of the state transition actor */
    String name() default TransitionActor.UNNAMED;
}
