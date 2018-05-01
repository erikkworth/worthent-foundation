package com.worthent.foundation.util.metadata;

import java.util.Map;
import java.util.Set;

/**
 * Specifies a container of type definitions. Implementations of this interface
 * hold data types. Data types are put into the dictionary with a reference
 * identifier so that they may be retrieved again later.
 * <p>
 * Type dictionaries are organized in a hierarchy. New dictionaries are always
 * created from an existing dictionary, which becomes the parent of the newly
 * created dictionary. Data types contained in the parent dictionary are
 * accessible from child dictionaries via the {@link #getType} method. The
 * default dictionary exposes a number of pre-defined property types for the
 * standard Java objects. Note that it is only possible to traverse the
 * dictionary hierarchy from children up through their parentage. Dictionaries
 * only keep references to their parents.
 * 
 * @author Erik K. Worth
 */
public interface TypeDictionary {

    /** Basic String type */
    String STRING = MetadataRsrc.TYPE_STRING.toString();

    /** Basic Boolean type */
    String BOOLEAN = MetadataRsrc.TYPE_BOOLEAN.toString();

    /** Basic Byte type */
    String BYTE = MetadataRsrc.TYPE_BYTE.toString();

    /** Basic Short Integer type */
    String SHORT = MetadataRsrc.TYPE_SHORT.toString();

    /** Basic Long Integer type */
    String LONG = MetadataRsrc.TYPE_LONG.toString();

    /** Basic Integer type */
    String INTEGER = MetadataRsrc.TYPE_INTEGER.toString();

    /** Basic Float type */
    String FLOAT = MetadataRsrc.TYPE_FLOAT.toString();

    /** Basic Double Float type */
    String DOUBLE = MetadataRsrc.TYPE_DOUBLE.toString();

    /** Basic Big Integer type */
    String BIG_INTEGER = MetadataRsrc.TYPE_BIG_INTEGER.toString();

    /** Basic Big Decimal type */
    String BIG_DECIMAL = MetadataRsrc.TYPE_BIG_DECIMAL.toString();

    /** Basic Date type */
    String DATE = MetadataRsrc.TYPE_DATE.toString();

    /** File Date type with a range validator suitable for electronic media */
    String FILE_DATE = MetadataRsrc.TYPE_FILE_DATE.toString();

    /** @return the unique ID of the type dictionary */
    String getId();

    /**
     * Create a new dictionary with a unique ID that may extend and override
     * types defined in this dictionary. The new dictionary is a child of this
     * parent dictionary.
     *
     * @param dictId the dictionary identifier
     * @return the new dictionary
     */
    TypeDictionary newDictionary(String dictId);

    /**
     * Create a new dictionary with a generated unique ID that may extend and
     * override types defined in this dictionary. The new dictionary is a child
     * of this parent dictionary.
     *
     * @return the new dictionary
     */
    TypeDictionary newDictionary();

    /**
     * @return the parent of this dictionary or <code>null</code> if this
     * dictionary has no parent.
     */
    TypeDictionary getParent();

    /**
     * Returns the property type definition from its reference in the dictionary
     * or <code>null</code> if not found.
     *
     * @param refId the referenced type identifier
     * @return the property type definition from its reference in the dictionary
     * or <code>null</code> if not found
     */
    DataType getType(String refId);

    /**
     * Puts the data type into the dictionary with the specified name. The
     * arguments must not be null. This method replaces any existing type
     * definition with the same reference ID as long as the type is declared in
     * this dictionary and not a parent dictionary.
     * 
     * @param refId the reference identifier used to retrieve the property type
     * @param type the property type definition
     */
    void putType(String refId, DataType type);

    /**
     * Puts the data types into the dictionary by their keys. The map must not
     * be <code>null</code> and the key and data types in the map must not be
     * <code>null</code>.
     * 
     * @param types the map of data types to put into the dictionary
     */
    void setTypes(Map<String, DataType> types);

    /**
     * Removes the specified type in the dictionary and returns the removed
     * type. This throws an exception if the specified type is present in the
     * parent dictionary or above in the dictionary hierarchy.
     * 
     * @param refId the reference identifier
     * @return the removed type or <code>null</code> if there is not such type
     *         in the dictionary
     * 
     * @throws MetadataException thrown when the referenced type is in the
     *         parent dictionary or above in the dictionary hierarchy
     */
    DataType removeType(String refId) throws MetadataException;

    /**
     * @return the set of type Identifiers for all the types in this dictionary
     * that are not in the default dictionary.
     */
    Set<String> getTypeIds();

}
