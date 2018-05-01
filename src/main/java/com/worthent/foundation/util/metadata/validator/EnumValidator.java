package com.worthent.foundation.util.metadata.validator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.worthent.foundation.util.i18n.ResourceKey;
import com.worthent.foundation.util.metadata.Validator;
import com.worthent.foundation.util.recorder.DataErrorReporter;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Validator used by enumerated types to report errors when the provided data is
 * not one of the enumerated literals.
 * 
 * @author Erik K. Worth
 */
public class EnumValidator extends ValidatorUtil implements Validator {

    /** Serial Version ID */
    private static final long serialVersionUID = 8185168151147473L;

    /** The set of allowed values for this type */
    private final Set<Object> allowedValues;

    /** The resource key used to report a validation error message */
    private final ResourceKey rsrcKey;
    
    /**
     * Construct the validator with the allowed string values
     *
     * @param allowedValues the allowed values for the enumerated type
     */
    public EnumValidator(final Collection<String> allowedValues) {
        this(allowedValues, ValidatorRsrc.INVALID_ENUM_VALUE);
    }

    /**
     * Construct the validator with the allowed string values and a resource key
     * for reporting errors
     *
     * @param allowedValues the allowed values for the enumerated type
     * @param errMsgResourceKey the resource key for the error message
     */
    public EnumValidator(
        final Collection<String> allowedValues,
        final ResourceKey errMsgResourceKey) {
        this.allowedValues = new HashSet<>(checkNotNull(allowedValues, "allowedValues must not be null"));
        this.rsrcKey = checkNotNull(errMsgResourceKey, "errMsgResourceKey must not be null");
    }

    @Override
    public void validate(final Object data, final DataErrorReporter errReporter) {
        if (null == data) {
            // Do not validate nulls
            return;
        }
        if (!allowedValues.contains(data)) {
            final Locale locale = getLocale();
            final String msg = rsrcKey.localize(locale);
            errReporter.reportError(msg);
        }
    }

}
