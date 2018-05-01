package com.worthent.foundation.util.metadata;

import com.worthent.foundation.util.i18n.AbstractResourceKey;
import com.worthent.foundation.util.i18n.MessageLocalizer;
import com.worthent.foundation.util.i18n.ResourceKey;

/** 
 * Declares string resources for use in this package
 *
 *  @author  Erik K. Worth
 */
public final class MetadataRsrc extends AbstractResourceKey {
    /** Shortcut for this class */
    private static final Class<MetadataRsrc> THIS_CLASS = MetadataRsrc.class;
    
    /** Resource bundle for this package */
    private static final String RSRC_BUNDLE = 
        THIS_CLASS.getPackage().getName() + ".metadata";
    
    /** Localizes message for this package. */
    static final MessageLocalizer LOCALIZER =
        MessageLocalizer.createMessageLocalizer(
            RSRC_BUNDLE, THIS_CLASS.getClassLoader());
    
    //
    // Key Prefixes
    //

    /** Type Code Prefix */
    private static final String TYPE_PREFIX = "TYPE_";
    
    /** Child Name Prefix */
    private static final String CHILD_NAME_PREFIX = "CHILD_TYPE_";
    
    //
    // Well-known Child Type Names
    //
    
    /** Resource key for the name of the list element type */
    static final ResourceKey NAME_LIST_ELEMENT_TYPE_KEY =
        new MetadataRsrc(CHILD_NAME_PREFIX + "ListElement");
    
    /** Resource key for the name of the map element type */
    static final ResourceKey NAME_MAP_ELEMENT_TYPE_KEY =
        new MetadataRsrc(CHILD_NAME_PREFIX + "MapElement");
    
    //
    // Type Codes
    //
    
    /** Resource key for the Array code */
    static final ResourceKey TYPE_ARRAY =
        new MetadataRsrc(TYPE_PREFIX + "Array");
    
    /** Resource key for the BigDecimal code */
    static final ResourceKey TYPE_BIG_DECIMAL =
        new MetadataRsrc(TYPE_PREFIX + "BigDecimal");
    
    /** Resource key for the BigInteger code */
    static final ResourceKey TYPE_BIG_INTEGER =
        new MetadataRsrc(TYPE_PREFIX + "BigInteger");
    
    /** Resource key for the Boolean code */
    static final ResourceKey TYPE_BOOLEAN =
        new MetadataRsrc(TYPE_PREFIX + "Boolean");
    
    /** Resource key for the Byte code */
    static final ResourceKey TYPE_BYTE =
        new MetadataRsrc(TYPE_PREFIX + "Byte");
    
    /** Resource key for the date code */
    static final ResourceKey TYPE_DATE =
        new MetadataRsrc(TYPE_PREFIX + "Date");
    
    /** Resource key for the Double code */
    static final ResourceKey TYPE_DOUBLE =
        new MetadataRsrc(TYPE_PREFIX + "Double");
    
    /** Resource key for the Enumerated type code */
    static final ResourceKey TYPE_ENUMERATED =
        new MetadataRsrc(TYPE_PREFIX + "Enumerated");
    
    /** Resource key for the file date code */
    static final ResourceKey TYPE_FILE_DATE =
        new MetadataRsrc(TYPE_PREFIX + "FileDate");
    
    /** Resource key for the Float code */
    static final ResourceKey TYPE_FLOAT =
        new MetadataRsrc(TYPE_PREFIX + "Float");
    
    /** Resource key for the Struct code */
    static final ResourceKey TYPE_STRUCT =
        new MetadataRsrc(TYPE_PREFIX + "Struct");
    
    /** Resource key for the Integer code */
    static final ResourceKey TYPE_INTEGER =
        new MetadataRsrc(TYPE_PREFIX + "Integer");
    
    /** Resource key for the List code */
    static final ResourceKey TYPE_LIST =
        new MetadataRsrc(TYPE_PREFIX + "List");
    
    /** Resource key for the Long code */
    static final ResourceKey TYPE_LONG =
        new MetadataRsrc(TYPE_PREFIX + "Long");
    
    /** Resource key for the Map code */
    static final ResourceKey TYPE_MAP =
        new MetadataRsrc(TYPE_PREFIX + "Map");
    
    /** Resource key for the Number code */
    static final ResourceKey TYPE_NUMBER =
        new MetadataRsrc(TYPE_PREFIX + "Number");
    
    /** Resource key for the primitive numeric code */
    static final ResourceKey TYPE_NUMERIC =
        new MetadataRsrc(TYPE_PREFIX + "Numeric");
    
    /** Resource key for the Object (any) code */
    static final ResourceKey TYPE_OBJECT =
        new MetadataRsrc(TYPE_PREFIX + "Object");
    
    /** Resource key for a reference type */
    static final ResourceKey TYPE_REFERENCE =
        new MetadataRsrc(TYPE_PREFIX + "Reference");
    
    /** Resource key for the Short code */
    static final ResourceKey TYPE_SHORT =
        new MetadataRsrc(TYPE_PREFIX + "Short");
    
    /** Resource key for the string code */
    static final ResourceKey TYPE_STRING =
        new MetadataRsrc(TYPE_PREFIX + "String");
    
    /** Hide the constructor */
    private MetadataRsrc(final String key) {
        super(key);
    }
    
    /** Overload to return the localizer declared in this class */
    protected final MessageLocalizer getMessageLocalizer() {
        return LOCALIZER;
    }
}
