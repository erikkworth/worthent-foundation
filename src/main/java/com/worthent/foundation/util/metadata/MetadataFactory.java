/**
 * Copyright 2000-2011 Worth Enterprises, Inc. All rights reserved.
 */
package com.worthent.foundation.util.metadata;

import com.worthent.foundation.util.metadata.internal.DataTypeFactoryImpl;
import com.worthent.foundation.util.metadata.internal.TypeDictionaryImpl;

/**
 * Metadata Factory class used to statically access factory implementations for
 * the data type factory and the default type dictionary.
 * 
 * @author Erik K. Worth
 * @version $Id: MetadataFactory.java 52 2011-04-04 05:11:28Z eworth $
 */
public final class MetadataFactory {

    /** An object registered to locate remote dictionary containers */
    private static RemoteDictionaryContainerResolver remoteDictResolver;

    /** @return the default data type dictionary */
    public static TypeDictionary getDefaultDictionary() {
        return TypeDictionaryImpl.DEFAULT_DICTIONARY;
    }

    /**
     * @return the single instance of the Data Type MetadataFactory.
     */
    public static DataTypeFactory getInstance() {
        return DataTypeFactoryImpl.INSTANCE;
    }

    /**
     * Registers an object able to locate remote dictionary containers
     * @param resolver the component able to locale remote dictionary containers
     */
    public static synchronized void setRemoteDictionaryContainerResolver(
        final RemoteDictionaryContainerResolver resolver) {
        remoteDictResolver = resolver;
    }

    /**
     * Returns an object able to locate remote dictionary containers
     *
     * @return an object able to locate remote dictionary containers
     * @throws MetadataException thrown when there is no resolver registered
     */
    public static synchronized RemoteDictionaryContainerResolver getRemoteDictionaryContainerResolver()
        throws MetadataException {
        if (null == remoteDictResolver) {
            throw new MetadataException(
                "No remote dictionary container resolver registered");
        }
        return remoteDictResolver;
    }

    // Never constructed
    private MetadataFactory() {
    }
}
