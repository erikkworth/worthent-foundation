package com.worthent.foundation.util.metadata;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Locale;

import com.worthent.foundation.util.i18n.ResourceKey;

/**
 * Enumerates supported property types.
 * 
 * @author Erik K. Worth
 * @version $Id: TypeCode.java 52 2011-04-04 05:11:28Z eworth $
 */
public enum TypeCode implements Serializable {

    /** Identifies an array */
    ARRAY(MetadataRsrc.TYPE_ARRAY),

    /** Identifies a Boolean type */
    BOOLEAN(MetadataRsrc.TYPE_BOOLEAN),

    /** Identifies the Java Date type */
    DATE(MetadataRsrc.TYPE_DATE),

    /** Identifies an enumerated type */
    ENUMERATED(MetadataRsrc.TYPE_ENUMERATED),

    /** Identifies a list */
    LIST(MetadataRsrc.TYPE_LIST),

    /** Identifies a map */
    MAP(MetadataRsrc.TYPE_MAP),

    /** Identifies the Java numeric type */
    NUMERIC(MetadataRsrc.TYPE_NUMERIC),

    /** Identifies any type */
    OBJECT(MetadataRsrc.TYPE_OBJECT),

    /** Identifies a reference to a type */
    REFERENCE(MetadataRsrc.TYPE_REFERENCE),

    /** Identifies the Java String type */
    STRING(MetadataRsrc.TYPE_STRING),

    /** Identifies a structure data type with fields */
    STRUCT(MetadataRsrc.TYPE_STRUCT);

    /**
     * Returns the string representation of the type code for the default local
     */
    public String toString() {
        return key.toString();
    }

    /**
     * Returns the string representation of the type code for the specified
     * locale
     * 
     * @param locale the locale in which the type code is rendered
     * @return the string representation of the type code for the specified
     *         locale
     */
    public String toString(final Locale locale) {
        return key.toString(locale);
    }

    //
    // Private Methods
    //

    /** The resource key used to localize the type code */
    private transient final ResourceKey key;

    /** Hide the constructor */
    TypeCode(final ResourceKey key) {
        this.key = key;
    }

    /**
     * Allows the type to be internalized so that the resource key is restored.
     * 
     * @return the enumerated value
     * @throws ObjectStreamException thrown when there is an error internalizing
     *         the type code
     */
    private Object readResolve() throws ObjectStreamException {
        return TypeCode.valueOf(this.name());
    }
} // TypeCode

