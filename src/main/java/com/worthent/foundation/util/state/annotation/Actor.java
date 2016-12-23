/**
 * Copyright 2000-2011 Worth Enterprises, Inc. All rights reserved.
 */
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
 * @version $Id: Actor.java 2 2011-11-28 00:10:06Z erik.k.worth@gmail.com $
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Actor {
    /** The name of the state transition actor */
    String name() default TransitionActor.UNNAMED;
}
