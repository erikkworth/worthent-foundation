package com.worthent.foundation.util.metadata.internal;

import java.util.HashMap;
import java.util.Map;

import com.worthent.foundation.util.metadata.TypeDictionary;

/**
 * Provides an in-memory store for all the type dictionaries declared in a given VM.
 * 
 * @author Erik K. Worth
 */
public class DictionaryStore {

    /** The single instance of this class */
    private static DictionaryStore INSTANCE = new DictionaryStore();

    /** @return  the single instance of this class */
    public static DictionaryStore getInstance() {
        return INSTANCE;
    }

    /** The map of dictionaries by their unique key */
    private final Map<String, TypeDictionary> dictionaries;

    /** Returns <code>true</code> when the specified dictionary is present */
    synchronized boolean hasDictionary(final String dictId) {
        return dictionaries.containsKey(dictId);
    }

    /**
     * Stores the type dictionary by its unique ID
     * 
     * @param dictId the dictionary ID
     * @param dict the type dictionary being stored
     */
    public synchronized void put(final String dictId, final TypeDictionary dict) {
        dictionaries.put(dictId, dict);
    }

    /**
     * Retrieves the type dictionary by its ID
     *
     * @param dictId the type dictionary identifier
     * @return the type dictionary or <code>null</code> when there is none by the specified name
     */
    public synchronized TypeDictionary get(final String dictId) {
        return dictionaries.get(dictId);
    }

    /** Hide the constructor to enforce the singleton pattern */
    private DictionaryStore() {
        dictionaries = new HashMap<>();
    }
}
