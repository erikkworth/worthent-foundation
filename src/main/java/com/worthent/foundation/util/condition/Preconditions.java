package com.worthent.foundation.util.condition;

import java.util.List;

/**
 * Static methods used to check for null and blank arguments.
 *
 * @author Erik K. Worth
 */
public class Preconditions {

    /**
     * Returns the input value when the provided value is not null, otherwise it throws an IllegalArgumentException.
     * @param value the input value
     * @param message the message to include the in the exception with string format placeholders
     * @param <T> the type of the value to check for null
     * @return the input value when the provided value is not null
     */
    public static <T> T checkNotNull(final T value, final String message) {
        if (null == value) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /**
     * Returns the input value when the provided value is not null, otherwise it throws an IllegalArgumentException.
     * @param value the input value
     * @param message the message to include the in the exception with string format placeholders
     * @param args the arguments to render into the string format placeholders
     * @param <T> the type of the value to check for null
     * @return the input value when the provided value is not null
     */
    public static <T> T checkNotNull(final T value, final String message, final Object... args) {
        if (null == value) {
            throw new IllegalArgumentException(String.format(message, args));
        }
        return value;
    }

    /**
     * Returns the input value when it is not null or a blank string, otherwise it throws an IllegalArgumentException
     * @param value the input value
     * @param message the message to include the in the exception with string format placeholders
     * @return the input value when it is not null or a blank string
     */
    public static String checkNotBlank(final String value, final String message) {
        final String nonNullValue = checkNotNull(value, message);
        if (nonNullValue.trim().length() == 0) {
            throw new IllegalArgumentException(message);
        }
        return nonNullValue;
    }

    /**
     * Returns the input value when it is not null or a blank string, otherwise it throws an IllegalArgumentException
     * @param value the input value
     * @param message the message to include the in the exception with string format placeholders
     * @param args the arguments to render into the string format placeholders
     * @return the input value when it is not null or a blank string
     */
    public static String checkNotBlank(final String value, final String message, final Object... args) {
        final String nonNullValue = checkNotNull(value, message, args);
        if (nonNullValue.trim().length() == 0) {
            throw new IllegalArgumentException(String.format(message, args));
        }
        return nonNullValue;
    }

    /**
     * Returns the input list when it is not null or empty, otherwise it throws an IllegalArgumentException
     * @param value the input list
     * @param message the message to include in the exception
     * @param <T> the list element type
     * @return the input list when it is not null or empty, otherwise it throws an IllegalArgumentException
     */
    public static <T> List<T> checkNotEmpty(final List<T> value, final String message) {
        final List<T> nonNullList = checkNotNull(value, message);
        if (nonNullList.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return nonNullList;
    }
}
