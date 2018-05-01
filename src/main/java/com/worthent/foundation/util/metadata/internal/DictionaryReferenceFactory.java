package com.worthent.foundation.util.metadata.internal;

import com.worthent.foundation.util.metadata.DataGetter;
import com.worthent.foundation.util.metadata.DictionaryReference;
import com.worthent.foundation.util.metadata.MetadataException;

/**
 * Specifies the method used to create a dictionary from its state data object.
 * All dictionary reference factories must have a public default constructor.
 * 
 * @author Erik K. Worth
 */
public interface DictionaryReferenceFactory {

    /**
     * Returns a dictionary reference from the data object state.
     * 
     * @param dataObject the data object holding the dictionary reference state
     * @return a dictionary reference from the data object state
     * @throws MetadataException thrown when there is an error restoring the
     *         dictionary reference from its state object
     */
    DictionaryReference restoreDictionaryReference(DataGetter dataObject)
        throws MetadataException;
}
