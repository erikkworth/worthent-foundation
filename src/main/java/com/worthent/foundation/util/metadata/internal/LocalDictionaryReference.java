package com.worthent.foundation.util.metadata.internal;

import java.util.Objects;

import com.worthent.foundation.util.metadata.TypeDictionary;

/**
 * Provides a reference to a data type dictionary in the local Java VM.
 * 
 * @author Erik K. Worth
 */
public class LocalDictionaryReference extends AbstractDictionaryReference {

    /** Serial Version ID */
    private static final long serialVersionUID = -8302656781540594628L;

    /** Shortcut to dictionary store holding type dictionaries */
    static final DictionaryStore DICT_STORE = DictionaryStore.getInstance();

    /** The reference to the type dictionary */
    private final TypeDictionary dict;

    /**
     * Constructs a dictionary reference for the specified type dictionary.
     * 
     * @param dict the type dictionary
     */
    public LocalDictionaryReference(final TypeDictionary dict) {
        super(LocalDictionaryReferenceFactory.class);
        this.dict = dict;
    }

    /** Returns <code>true</code> if the other has the same state */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LocalDictionaryReference)) {
            return false;
        }
        LocalDictionaryReference that = (LocalDictionaryReference) other;
        final String dictId = getLocalReferenceId();
        return (dictId.equals(that.getLocalReferenceId()) && super.equals(that));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFactoryClassName(), getLocalReferenceId());
    }

    @Override
    public String getLocalReferenceId() {
        return dict.getId();
    }

    @Override
    public TypeDictionary getDictionary() {
        return dict;
    }

    @Override
    public boolean referencesDictionary(final TypeDictionary dictionary) {
        return null != dictionary && dictionary == dict;
    }

}
