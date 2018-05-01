package com.worthent.foundation.util.metadata.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.worthent.foundation.util.metadata.DataType;
import com.worthent.foundation.util.metadata.DictionaryReference;
import com.worthent.foundation.util.metadata.MetadataException;
import com.worthent.foundation.util.metadata.TypeDictionary;

/**
 * Implements the type dictionary interface.
 * 
 * @author Erik K. Worth
 */
public class TypeDictionaryImpl implements TypeDictionary {

    /** Logger for this class */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(TypeDictionaryImpl.class);

    /** The ID of the default type dictionary */
    private static final String DEFAULT_TYPE_DICTIONARY =
        "DefaultTypeDictionary";

    /** Default type dictionary */
    public static final TypeDictionaryImpl DEFAULT_DICTIONARY =
        new TypeDictionaryImpl();

    /** The unique ID for this type dictionary */
    private final String dictId;

    /** The parent of this dictionary */
    private final TypeDictionaryImpl parent;

    /** The map holding the declared data types */
    private final Map<String, DataType> types;

    @Override
    public String getId() {
        return dictId;
    }

    @Override
    public TypeDictionary getParent() {
        return parent;
    }

    @Override
    public DataType getType(final String refId) {
        final DataType type = types.get(refId);
        return (null == type)
            ? (null == parent) ? null : parent.getType(refId)
            : type;
    }

    @Override
    public TypeDictionary newDictionary() {
        return new TypeDictionaryImpl(UUID.randomUUID().toString(), this);
    }

    @Override
    public TypeDictionary newDictionary(final String dictId) {
        // Return an existing dictionary if it already exists
        final TypeDictionary existing =
            DictionaryStore.getInstance().get(dictId);
        if (null != existing) {
            return existing;
        }
        // Otherwise, create a new one
        return new TypeDictionaryImpl(dictId, this);
    }

    @Override
    public void putType(final String refId, final DataType type) {
        if (null == parent) {
            throw new IllegalStateException("The default dictionary is immutable.");
        }
        // Make sure only types that extend the abstract data type are added
        AbstractDataType.assertIsClass(type, AbstractDataType.class);
        types.put(refId, type);
    }

    @Override
    public void setTypes(final Map<String, DataType> types) {
        if (null == parent) {
            throw new IllegalStateException(
                "The default dictionary is imutable.");
        }
        if (null == types) {
            throw new IllegalArgumentException("types must not be null");
        }
        for (final DataType type : types.values()) {
            // Make sure only types that extend the abstract data type are added
            AbstractDataType.assertIsClass(type, AbstractDataType.class);
        }
        this.types.putAll(types);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Registered types with IDs, " + types.keySet());
        }
    }

    @Override
    public DataType removeType(final String refId) throws MetadataException {
        if (null == parent) {
            throw new IllegalStateException(
                "The default dictionary is imutable.");
        }
        return types.remove(refId);
    }

    @Override
    public Set<String> getTypeIds() {
        final Set<String> typeIds = new HashSet<>();
        TypeDictionaryImpl dict = this;
        while ((null != dict) && (dict != DEFAULT_DICTIONARY)) {
            typeIds.addAll(dict.types.keySet());
            dict = dict.parent;
        }
        return typeIds;
    }

    /** Constructs the default dictionary */
    private TypeDictionaryImpl() {
        this(DEFAULT_TYPE_DICTIONARY, null);

        // Define the simple types
        types.put(BIG_DECIMAL, SimpleType.BIG_DECIMAL);
        types.put(BIG_INTEGER, SimpleType.BIG_INTEGER);
        types.put(BOOLEAN, SimpleType.BOOLEAN);
        types.put(BYTE, SimpleType.BYTE);
        types.put(DATE, DateType.DATE);
        types.put(DOUBLE, SimpleType.DOUBLE);
        types.put(INTEGER, SimpleType.INTEGER);
        types.put(FILE_DATE, DateType.FILE_DATE);
        types.put(FLOAT, SimpleType.FLOAT);
        types.put(LONG, SimpleType.LONG);
        types.put(SHORT, SimpleType.SHORT);
        types.put(STRING, SimpleType.STRING);
    }

    /**
     * Constructs a child dictionary of the specified parent
     *
     * @param dictId the dictionary identifier
     * @param parent the parent dictionary
     */
    protected TypeDictionaryImpl(
        final String dictId,
        final TypeDictionaryImpl parent) {
        if (DictionaryStore.getInstance().hasDictionary(dictId)) {
            throw new IllegalArgumentException("Dictionary ID not unique: " +
                dictId);
        }
        this.dictId = dictId;
        this.parent = parent;
        types = new HashMap<>();

        // When a new dictionary is created on an existing (non-default)
        // dictionary, there may be reference types that refer to values in
        // the existing dictionary. If the caller puts a new type definition
        // into the new dictionary with the same ID as a type in the existing
        // on, we want to make sure the new type is used instead of the existing
        // type. That means we need to update the dictionary references in any
        // existing type to make sure it references the new dictionary. The
        // type definitions with updated dictionary references are copied into
        // the new dictionary to make sure references to the existing types
        // are preserved.
        if ((null != parent) && (null != parent.getParent())) {
            // This is not the default dictionary and neither is the parent
            final DictionaryReference dictRef =
                new LocalDictionaryReference(this);
            for (Map.Entry<String, DataType> entry : parent.types.entrySet()) {
                final AbstractDataType type =
                    (AbstractDataType) entry.getValue();
                if (type.referencesDictionary(parent)) {
                    final AbstractDataType copy =
                        (AbstractDataType) type.deepCopy();
                    copy.replaceDictionaryReference(parent, dictRef);
                    // Override the type by putting the copy in this dictionary
                    // with the same ID as the original
                    types.put(entry.getKey(), copy);
                }
            }
        }
    }

}
