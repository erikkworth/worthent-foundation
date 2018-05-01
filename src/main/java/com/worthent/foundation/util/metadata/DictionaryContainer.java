package com.worthent.foundation.util.metadata;

/**
 * Components that contain dictionaries implement this interface. It allows a
 * dictionary reference to obtain a dictionary from such components.
 * 
 * @author Erik K. Worth
 */
public interface DictionaryContainer {

    /**
     * Returns a type dictionary for the specified dictionary reference ID.
     * 
     * @param dictRef identifies the dictionary within the dictionary container
     * @return a type dictionary for the specified dictionary reference ID
     * @throws MetadataException thrown when there is no dictionary for the
     *             reference ID or there is an error retrieving the dictionary
     */
    TypeDictionary getTypeDictionary(String dictRef) throws MetadataException;
}
