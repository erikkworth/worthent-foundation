package com.worthent.foundation.util.metadata.validator;

import com.worthent.foundation.util.i18n.AbstractResourceKey;
import com.worthent.foundation.util.i18n.MessageLocalizer;
import com.worthent.foundation.util.i18n.ResourceKey;

/**
 * Declares string resources for use in this package
 * 
 * @author Erik K. Worth
 */
public final class ValidatorRsrc extends AbstractResourceKey {
    /** Shortcut for this class */
    private static final Class<ValidatorRsrc> THIS_CLASS = ValidatorRsrc.class;

    /** Resource bundle for this package */
    private static final String RSRC_BUNDLE =
        THIS_CLASS.getPackage().getName() + ".validator";

    /** Localizes message for this package. */
    protected static final MessageLocalizer LOCALIZER =
        MessageLocalizer.createMessageLocalizer(RSRC_BUNDLE, THIS_CLASS
            .getClassLoader());

    //
    // Key Prefixes
    //

    /** Validator Error Message Prefix */
    private static final String MSG_PREFIX = "ERR_";

    /** Used to report class cast exceptions. Arguments:
     * <ol>
     * <li>Expected class name
     * <li>Actual class name
     * </ol>
     */
    static final ResourceKey CLASS_CAST_ERROR =
        new ValidatorRsrc(MSG_PREFIX + "ClassCastError");

    /** Used to report an invalid value for an enumerated type. No arguments */
    static final ResourceKey INVALID_ENUM_VALUE =
        new ValidatorRsrc(MSG_PREFIX + "InvalidEnumValue");

    /** Used to report a required field without a value */
    static final ResourceKey MISSING_REQUIRED_VALUE =
        new ValidatorRsrc(MSG_PREFIX + "MissingRequiredValue");

    /**
     * Used to report a value outside the range of legal values.
     */
    static final ResourceKey VALUE_OUTSIDE_LEGAL_RANGE =
        new ValidatorRsrc(MSG_PREFIX + "ValueOutsideLegalRange");

    /**
     * Used to report when a field is not greater than or equal to another.
     */
    static final ResourceKey NOT_GREATER_THAN_OR_EQUAL =
        new ValidatorRsrc(MSG_PREFIX + "NotGreaterThanOrEqual");

    /**
     * Resource key for the error message reporting a regular expression match
     * failure
     */
    static final ResourceKey REG_EX_MATCH_FAILURE =
        new ValidatorRsrc(MSG_PREFIX + "RegExMatchFailure");

    /**
     * Resource key for the error message reporting a black list word match
     * failure
     */
    static final ResourceKey BLACK_LIST_MATCH =
        new ValidatorRsrc(MSG_PREFIX + "BlackListMatch");
    
    /** Hide the constructor */
    private ValidatorRsrc(final String key) {
        super(key);
    }

    /** Overload to return the localizer declared in this class */
    protected final MessageLocalizer getMessageLocalizer() {
        return LOCALIZER;
    }
}
