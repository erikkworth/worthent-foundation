package com.worthent.foundation.util.metadata.validator;

import java.util.Date;
import java.util.Locale;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.i18n.ResourceKey;
import com.worthent.foundation.util.metadata.Validator;
import com.worthent.foundation.util.recorder.DataErrorReporter;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Validates a comparable value to make sure it is within a range of values.
 * 
 * @author Erik K. Worth
 */
public class RangeValidator<E> extends ValidatorUtil implements Validator {

    /** The upper bound on reasonable dates */
    public static final Date TWO_YEARS_FROM_NOW =
        new Date(new Date().getTime() + (1000L * 60L * 60L * 24L * 365L * 2L));

    /** Serial Version ID */
    private static final long serialVersionUID = -4599723245420842051L;

    /** The minimum allowed value (inclusive) */
    private final E lowerBound;

    /** The maximum allowed value (inclusive) */
    private final E upperBound;

    /** Resource key for validation message. */
    private final ResourceKey messageRsrcKey;
    
    /**
     * Construct the validator with the values that inclusively define the legal
     * range of values
     * 
     * @param lowerBound the lowest legal value
     * @param upperBound the highest legal value
     */
    public RangeValidator(final E lowerBound, final E upperBound) {
        this(lowerBound, upperBound, ValidatorRsrc.VALUE_OUTSIDE_LEGAL_RANGE);
    }

    /**
     * Construct the validator with the values that inclusively define the legal
     * range of values
     * 
     * @param lowerBound the lowest legal value
     * @param upperBound the highest legal value
     * @param messageRsrcKey the resource key for the custom error message. The
     *        key must identify a text pattern with three place holders: the
     *        first is for the value passed in, the second is for the lower
     *        bound, and the third is for the upper bound.
     */
    public RangeValidator(
        @NotNull final E lowerBound,
        @NotNull final E upperBound,
        @NotNull final ResourceKey messageRsrcKey) {
        this.lowerBound = checkNotNull(lowerBound, "lowerBound must not be null");
        this.upperBound = checkNotNull(upperBound, "upperBound must not be null");
        this.messageRsrcKey = checkNotNull(messageRsrcKey, "messageRsrcKey must not be null");
    }

    /** @return the minimum allowed value (inclusive) */
    protected final E getLowerBound() {
        return lowerBound;
    }

    /** @return the maximum allowed value (inclusive) */
    protected final E getUpperBound() {
        return upperBound;
    }

    /**
     * Return the string form of the value. This is used to generate
     * an error message. It may be overloaded to produce a nicer string
     * representation of a boundaries and value in the error message.
     *
     * @param value the object argument
     * @return the string form of the bound and value
     */
    protected String getStringForm(final Object value) {
        return (null == value) ? null : value.toString();
    }
    
    /**
     * @return the resource key used to localize the validation error message.
     */
    protected ResourceKey getMessageResourceKey() {
        return messageRsrcKey;
    }

    @Override
    public void validate(final Object data, final DataErrorReporter errReporter) {
        if (null == data) {
            return;
        }
        @SuppressWarnings("unchecked")
        final Comparable<E> value = safeCast(data, Comparable.class, errReporter);
        if (null == value) {
            return;
        }
        if ((0 > value.compareTo(lowerBound)) ||
            (0 < value.compareTo(upperBound))) {
            final Locale locale = getLocale();
            final String msg = messageRsrcKey.localize(
                    locale, getStringForm(data), getStringForm(lowerBound), getStringForm(upperBound));
            errReporter.reportError(msg);
        }
    }
}
