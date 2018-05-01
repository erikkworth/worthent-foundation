package com.worthent.foundation.util.metadata;

import java.io.Serializable;

/**
 * Specifies the methods implemented by objects able to locate a
 * {@link TypeDictionary}.
 * 
 * @author Erik K. Worth
 */
public interface DictionaryReference extends Serializable {
    
    /**
     * @return the local reference identifier for the type dictionary.
     */
    String getLocalReferenceId();

    /**
     * Returns a type dictionary.
     * 
     * @return a type dictionary
     * 
     * @throws MetadataException thrown when there is an error retrieving the
     *         type dictionary
     */
    TypeDictionary getDictionary() throws MetadataException;

    /**
     * Returns <code>true</code> if the dictionary reference provides a
     * reference to the specified dictionary
     * 
     * @param dictionary the subject dictionary
     * @return <code>true</code> if the dictionary reference provides a
     *         reference to the specified dictionary
     */
    boolean referencesDictionary(TypeDictionary dictionary);
    
}
