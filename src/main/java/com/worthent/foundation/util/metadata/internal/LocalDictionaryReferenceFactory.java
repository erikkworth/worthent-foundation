package com.worthent.foundation.util.metadata.internal;

import java.util.HashMap;
import java.util.Map;

import com.worthent.foundation.util.metadata.DataGetter;
import com.worthent.foundation.util.metadata.DictionaryReference;
import com.worthent.foundation.util.metadata.MetadataException;
import com.worthent.foundation.util.metadata.TypeDictionary;

/**
 * Dictionary Reference Factory for the Local Dictionary Reference.
 * 
 * @author Erik K. Worth
 */
public class LocalDictionaryReferenceFactory implements
    DictionaryReferenceFactory {

    /** Map of dictionary referenced by their dictionary key */
    private Map<String, DictionaryReference> dictRefLookup;

    public LocalDictionaryReferenceFactory() {
        dictRefLookup = new HashMap<>();
    }

    @Override
    public synchronized DictionaryReference restoreDictionaryReference(
        final DataGetter dataObject) throws MetadataException {
        if (null == dataObject) {
            throw new IllegalArgumentException("null dataObject");
        }
        final MetadataMetadata metaMeta = MetadataMetadata.getInstance();
        final String dictKey = metaMeta.getLocalDictionaryReference(dataObject);
        DictionaryReference dictRef = dictRefLookup.get(dictKey);
        if (null == dictRef) {
            final TypeDictionary dict =
                LocalDictionaryReference.DICT_STORE.get(dictKey);
            if (null == dict) {
                throw new MetadataException(
                    "No dictionary registered in store by key, '" + dictKey +
                        "'");
            }
            dictRef = new LocalDictionaryReference(dict);
            dictRefLookup.put(dictKey, dictRef);
        }
        return dictRef;
    }

}
