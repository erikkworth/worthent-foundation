package com.worthent.foundation.util.metadata.validator;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.worthent.foundation.util.recorder.DataErrorReporter;

/**
 * Provides some helper methods to validators.
 * 
 * @author Erik K. Worth
 */
public abstract class ValidatorUtil {

    /** The logger for this class */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ValidatorUtil.class);

    /**
     * Casts the provided data to the specified class or report an error
     * @param <T> The target type
     * @param data the data to cast to the target type
     * @param clazz the target class
     * @param errReporter the component able to capture any errors
     * @return the object cast to the target type
     */
    @SuppressWarnings("unchecked")
    protected static <T> T safeCast(
        final Object data,
        final Class<T> clazz,
        final DataErrorReporter errReporter) {
        try {
            return (T) data;
        } catch (final ClassCastException exc) {
            final String msg =
                ValidatorRsrc.CLASS_CAST_ERROR.localize(
                    getLocale(),
                    clazz.getName(),
                    data.getClass().getName());
            errReporter.reportError(msg, exc);
            return null;
        }
    }

    /** @return the locale provided in the execution context */
    protected static Locale getLocale() {
        return Locale.getDefault();
    }

    /**
     * Reports the given error to the error reporter by properly setting the
     * data id.
     * 
     * @param errMsg The error message.
     * @param errField The field which this error should be associated with.
     * @param errReporter the error reporter used to associated the errors with
     *            some path or key for the data being validated
     */
    protected static void reportError(
        final String errMsg,
        final String errField,
        final DataErrorReporter errReporter) {
        final String priorDataId = errReporter.getDataId();
        try {
            final StringBuilder newDataId = new StringBuilder();
            if (null != priorDataId) {
                newDataId.append(priorDataId);
                if (']' != newDataId.charAt(newDataId.length() - 1)) {
                    newDataId.append('.');
                }
            }
            newDataId.append(errField);
            errReporter.setDataId(newDataId.toString());
            errReporter.reportError(errMsg);
        } finally {
            errReporter.setDataId(priorDataId);
        }
    }

    /** Hide Constructor */
    protected ValidatorUtil() {
        // Empty
    }
}
