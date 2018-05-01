package com.worthent.foundation.util.metadata.internal;

import java.io.ObjectStreamException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.worthent.foundation.util.metadata.DataGetter;
import com.worthent.foundation.util.metadata.DictionaryContainer;
import com.worthent.foundation.util.metadata.DictionaryReference;
import com.worthent.foundation.util.metadata.MetadataException;
import com.worthent.foundation.util.metadata.MetadataFactory;
import com.worthent.foundation.util.metadata.RemoteDictionaryContainerResolver;
import com.worthent.foundation.util.metadata.TypeDictionary;

/**
 * Implements a dictionary reference able to obtain a type definition from the
 * dictionary backing a service.
 * 
 * @author Erik K. Worth
 */
public class RemoteDictionaryReference extends AbstractDictionaryReference {

    /**
     * Dictionary Reference Factory for the Remote Dictionary Reference.
     */
    public static class RemoteDictionaryReferenceFactory implements
        DictionaryReferenceFactory {

        /** Map of dictionary referenced by their dictionary key */
        private Map<String, DictionaryReference> dictRefLookup;

        /** Default public constructor */
        public RemoteDictionaryReferenceFactory() {
            dictRefLookup = new HashMap<>();
        }

        @Override
        public synchronized DictionaryReference restoreDictionaryReference(
            final DataGetter dataObject) throws MetadataException {
            if (null == dataObject) {
                throw new IllegalArgumentException("null dataObject");
            }
            final MetadataMetadata metaMeta = MetadataMetadata.getInstance();
            final String remoteDictRef =
                metaMeta.getRemoteDictionaryReference(dataObject);
            final String localDictRef =
                metaMeta.getLocalDictionaryReference(dataObject);
            final String dictKey = remoteDictRef + localDictRef;
            DictionaryReference dictRef = dictRefLookup.get(dictKey);
            if (null == dictRef) {
                final DictionaryContainer dictContainer;
                final RemoteDictionaryContainerResolver resolver =
                    MetadataFactory.getRemoteDictionaryContainerResolver();
                dictContainer =
                    resolver.getRemoteDictionaryContainer(remoteDictRef);
                dictRef =
                    new RemoteDictionaryReference(dictContainer, localDictRef);
                dictRefLookup.put(dictKey, dictRef);
            }
            return dictRef;
        }

    }

    /** The key to the dictionary in the local dictionary store */
    private final String localDictRef;

    /** The remote dictionary container */
    private final DictionaryContainer remoteDictContainer;

    /**
     * Construct from a remote dictionary container and a dictionary reference.
     *
     * @param remoteDictContainer the remote dictionary container
     * @param localDictRef the dictionary reference
     */
    protected RemoteDictionaryReference(
            final DictionaryContainer remoteDictContainer,
            final String localDictRef) {
        super(RemoteDictionaryReferenceFactory.class);
        this.remoteDictContainer = remoteDictContainer;
        this.localDictRef = localDictRef;
    }

    /** Copy Constructor */
    private RemoteDictionaryReference(final RemoteDictionaryReference other) {
        super(other);
        remoteDictContainer = other.remoteDictContainer;
        localDictRef = other.localDictRef;
    }

    /** Returns <code>true</code> if the other has the same state */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RemoteDictionaryReference)) {
            return false;
        }
        RemoteDictionaryReference that = (RemoteDictionaryReference) other;
        return (localDictRef.equals(that.localDictRef) &&
            remoteDictContainer.equals(that.remoteDictContainer) && super.equals(that));
    }

    /** Returns a hash code */
    @Override
    public int hashCode() {
        return Objects.hash(getFactoryClassName(), localDictRef, remoteDictContainer);
    }

    @Override
    public TypeDictionary getDictionary() throws MetadataException {
        try {
            return remoteDictContainer.getTypeDictionary(localDictRef);
        } catch (final MetadataException exc) {
            throw new MetadataException(
                "Error retriving dictionary for dict ID, '" + localDictRef +
                    "'",
                exc);
        }
    }

    @Override
    public String getLocalReferenceId() {
        return localDictRef;
    }

    @Override
    public boolean referencesDictionary(final TypeDictionary dictionary) {
        if (null == dictionary) {
            return false;
        }
        try {
            return dictionary == getDictionary();
        } catch (MetadataException ignore) {
            return false;
        }
    }
}
