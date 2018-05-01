package com.worthent.foundation.util.metadata.internal;

import com.worthent.foundation.util.metadata.DictionaryReference;

import java.util.Objects;

/**
 * Base class for dictionary reference implementations.
 * 
 * @author Erik K. Worth
 */
public abstract class AbstractDictionaryReference implements
    DictionaryReference {

    /** Serial Version ID */
    private static final long serialVersionUID = -7581461607185300931L;

    /**
     * The data object field identifying the dictionary reference factory class
     * name
     */
    static final String FACTORY_CLASS_NAME = "FactoryClassName";

    /** The field holding the local dictionary reference */
    static final String LOCAL_DICT_REF = "LocalDictRef";

    /** The field holding the remote dictionary reference */
    static final String REMOTE_DICT_REF = "RemoteDictRef";

    /**
     * Class name of factory able to restore a dictionary reference from its
     * data object
     */
    private final String factoryClassName;

    /**
     * Construct the base class for dictionary references.
     * 
     * @param factoryClass the factory class able to restore instances of the
     *        derived class
     */
    protected AbstractDictionaryReference(
        final Class<? extends DictionaryReferenceFactory> factoryClass) {
        factoryClassName = factoryClass.getName();
    }
    
    /**
     * Copy constructor
     *
     * @param other the other reference to copy
     */
    protected AbstractDictionaryReference(final AbstractDictionaryReference other) {
        factoryClassName = other.factoryClassName;
    }
    
    /** Returns <code>true</code> if the other has the same state */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractDictionaryReference)) {
            return false;
        }
        AbstractDictionaryReference that = (AbstractDictionaryReference) other;
        return factoryClassName.equals(that.factoryClassName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getFactoryClassName());
    }

    /**
     * @return the class name of factory able to restore a dictionary reference
     * from its data object
     */
    protected String getFactoryClassName() {
        return factoryClassName;
    }
}
