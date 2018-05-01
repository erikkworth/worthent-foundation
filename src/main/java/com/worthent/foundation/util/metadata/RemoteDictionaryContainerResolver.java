package com.worthent.foundation.util.metadata;

/**
 * Service provider interface for an object able to return a proxy for a remote
 * dictionary container.
 * 
 * @author Erik K. Worth
 */
public interface RemoteDictionaryContainerResolver {

    /**
     * Returns a remote dictionary container for the specified remote dictionary
     * reference ID
     * 
     * @param remoteDictRef identifies the remote dictionary container
     * @return a remote dictionary container for the specified remote dictionary
     *         reference ID
     * @throws MetadataException thrown when there is an error returning the
     *             proxy for the remote dictionary reference
     */
    DictionaryContainer getRemoteDictionaryContainer(String remoteDictRef)
        throws MetadataException;
}
