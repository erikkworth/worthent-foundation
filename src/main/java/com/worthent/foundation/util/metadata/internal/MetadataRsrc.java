package com.worthent.foundation.util.metadata.internal;

import com.worthent.foundation.util.i18n.AbstractResourceKey;
import com.worthent.foundation.util.i18n.MessageLocalizer;
import com.worthent.foundation.util.i18n.ResourceKey;

/** 
 * Declares string resources for use in this package
 *
 *  @author  Erik K. Worth
 */
public final class MetadataRsrc extends AbstractResourceKey
{
    /** Shortcut for this class */
    private static final Class<MetadataRsrc> THIS_CLASS = MetadataRsrc.class;
    
    /** Resource bundle for this package */
    private static final String RSRC_BUNDLE = 
        THIS_CLASS.getPackage().getName() + ".metadata";
    
    /** Localizes message for this package. */
    protected static final MessageLocalizer LOCALIZER = 
        MessageLocalizer.createMessageLocalizer(
            RSRC_BUNDLE, THIS_CLASS.getClassLoader());
    
    //
    // Key Prefixes
    //

    /** Validator Error Message Prefix */
    private static final String ERR_PREFIX = "ERR_";
    
    /** Used to report an internal server error. No arguments. */
    static final ResourceKey INTERNAL_ERROR = 
        new MetadataRsrc(ERR_PREFIX + "InternalError");
    
    /**
     * Report an error when trying to convert a string to a value of a given
     * type.  Arguments: 
     * <ol>
     * <li>The type's type code
     * <li>The invalid value
     * </ol>
     */
    static final ResourceKey CONVERSION_ERROR =
        new MetadataRsrc(ERR_PREFIX + "ConversionError");

    
    /** Hide the constructor */
    private MetadataRsrc(final String key) {
        super(key);
    }
    
    /** Overload to return the localizer declared in this class */
    protected final MessageLocalizer getMessageLocalizer() {
        return LOCALIZER;
    }
}
