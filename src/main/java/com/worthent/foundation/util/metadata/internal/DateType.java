package com.worthent.foundation.util.metadata.internal;

import java.text.Format;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

import com.worthent.foundation.util.lang.StringUtils;
import com.worthent.foundation.util.metadata.DataGetter;
import com.worthent.foundation.util.metadata.DataType;
import com.worthent.foundation.util.metadata.MetadataException;
import com.worthent.foundation.util.metadata.TypeCode;
import com.worthent.foundation.util.metadata.Validator;
import com.worthent.foundation.util.metadata.validator.RangeValidator;

/**
 * Type metadata for dates.
 * 
 * @author Erik K. Worth
 */
public class DateType extends SimpleType {

    /** Serial Version ID */
    private static final long serialVersionUID = 7953095128356651079L;

    /** Validator for dates between 1970 and 10 years from today */
    private static final class FileDateRangeValidator extends
        RangeValidator<Date> {

        /** Serial Version ID */
        private static final long serialVersionUID = -6489347974035431673L;
        
        /** The number of milliseconds in 100 years */
        private static final long ONE_HUNDRED_YEARS = 1000L * 60L * 60L * 24L * 365L * 100L;

        /**
         * Create a validator that makes sure dates are not before 1970 and are
         * not after 100 years from now.
         */
        public FileDateRangeValidator() {
            super(new Date(0), new Date(System.currentTimeMillis() + ONE_HUNDRED_YEARS));
        }

        @Override
        protected String getStringForm(final Object value) {
            if (value instanceof Date) {
                return ISO_INSTANT.format(((Date) value).toInstant());
            } else {
                return super.getStringForm(value);
            }
        }
    }

    /** Single instance of the date range validator. */
    private static final Validator FILE_DATE_RANGE_VALIDATOR =
        new FileDateRangeValidator();

    /** Definition of the java Date type. */
    static final SimpleType DATE = new DateType();

    /** Definition of date used for electronic media with a range validator */
    static final SimpleType FILE_DATE = new DateType(FILE_DATE_RANGE_VALIDATOR);

    private static final DateTimeFormatter ISO_INSTANT = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendInstant(3)
            .toFormatter();

    /**
     * Construct the date type.
     */
    DateType() {
        super(TypeCode.DATE, java.util.Date.class.getName());
    }

    /** Create a date with a validator */
    DateType(final Validator validator) {
        super(TypeCode.DATE, java.util.Date.class.getName());
        addValidator(validator);
    }

    /**
     * Copy Constructor.
     * 
     * @param other the other date type to copy.
     */
    private DateType(final DateType other) {
        super(other);
    }

    /**
     * Construct from state data object
     *
     * @param dataObject the data object holding the information used to define this date type
     * @throws MetadataException thrown when there is an error defining the type from the information in the data object
     */
    protected DateType(final DataGetter dataObject) throws MetadataException {
        super(dataObject);
    }

    /**
     * Return the string representation of the specified data object.
     * 
     * @param data the data object
     * @param format the format to use or <code>null</code> to use the default
     *        date format object
     * @return the string representation of the specified data object
     */
    @Override
    protected String asString(final Object data, final Format format)
        throws MetadataException {
        if (null == data) {
            return null;
        }
        if (null == format) {
            try {
                return ISO_INSTANT.format(((Date) data).toInstant());
            } catch (final Exception exc) {
                throw new MetadataException("There was an error formatting the provided date", exc);
            }
        }
        return super.asString(data, format);
    }

    /**
     * Creates an instance of the date from its string representation
     * 
     * @param value the string value for the object
     * @return a new instance of the object using its String constructor
     * @throws MetadataException thrown when there is an error creating a copy
     *         of the date
     */
    @Override
    protected Object fromString(final String value) throws MetadataException {
        final String strValue = StringUtils.trimToNull(value);
        if (null == strValue) {
            return null;
        }
        try {
            final Instant instant = Instant.parse(strValue);
            return new Date(instant.toEpochMilli());
        } catch (final Exception exc) {
            throw new MetadataException("The value, '" + strValue + "', cannot be converted to a date", exc);
        }
    }

    /**
     * Returns a deep copy of the specified date.
     * 
     * @param data the data object to copy
     * @return a deep copy of the specified data
     * @throws MetadataException thrown when there is an error making a copy
     */
    @Override
    protected Object deepCopy(final Object data) throws MetadataException {
        if (null == data) {
            return null;
        }
        final Date date = safeCast(data);
        return new Date(date.getTime());
    }

    @Override
    public DataType deepCopy() {
        return new DateType(this);
    }
}
