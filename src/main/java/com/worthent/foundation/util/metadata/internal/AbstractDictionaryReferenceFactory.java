package com.worthent.foundation.util.metadata.internal;

import java.util.HashMap;
import java.util.Map;

import com.worthent.foundation.util.lang.StringUtils;
import com.worthent.foundation.util.metadata.DataGetter;
import com.worthent.foundation.util.metadata.DictionaryReference;
import com.worthent.foundation.util.metadata.MetadataException;

/**
 * Abstract Factory fore Dictionary Reference Factories. This class constructs a
 * Dictionary Reference Factory from its class name expected to be provided in
 * the data object in a well-known field.
 * 
 * @author Erik K. Worth
 */
public class AbstractDictionaryReferenceFactory {

    /** Shortcut for the class loader that loaded this class */
    private static final ClassLoader CLASS_LOADER =
        AbstractDictionaryReferenceFactory.class.getClassLoader();

    /** The single instance of this class */
    private static final AbstractDictionaryReferenceFactory INSTANCE =
        new AbstractDictionaryReferenceFactory();

    /** @return the single instance of this class */
    public static AbstractDictionaryReferenceFactory getInstance() {
        return INSTANCE;
    }

    /** Factory cache keyed by class name. Synchronized access. */
    private final Map<String, DictionaryReferenceFactory> factories;

    /**
     * Retrieves the dictionary reference factory and uses it to restore the
     * dictionary reference from the provided data object holding its state
     * information.
     * 
     * @param dataObject state data object for the dictionary reference
     * @return the dictionary reference
     * @throws MetadataException thrown when there is an error restoring the
     *         dictionary reference
     */
    public DictionaryReference restore(final DataGetter dataObject)
        throws MetadataException {
        final DictionaryReferenceFactory factory = getFactory(dataObject);
        return factory.restoreDictionaryReference(dataObject);
    }

    /**
     * Returns the factory able to create dictionary references from the
     * provided state object.
     * 
     * @param dataObject state data object for a dictionary reference
     * @return the factory able to create dictionary references from the
     *         provided state object
     * @throws MetadataException thrown when there is an error returning the
     *         factory
     */
    private synchronized DictionaryReferenceFactory getFactory(
        final DataGetter dataObject) throws MetadataException {
        final String rawClassName =
            dataObject.get(AbstractDictionaryReference.FACTORY_CLASS_NAME);
        final String className = StringUtils.trimToNull(rawClassName);
        if (null == className) {
            throw new MetadataException(
                "Dictionary Reference missing factory class name in data object");
        }
        DictionaryReferenceFactory factory = factories.get(className);
        if (null == factory) {
            // Not yet cached, so create it and cache it.
            try {
                final Class<?> factoryClass = CLASS_LOADER.loadClass(className);
                final Object obj = factoryClass.newInstance();
                factory = (DictionaryReferenceFactory) obj;
            } catch (final Exception exc) {
                throw new MetadataException(
                    "Unable to create dictionary reference factory from its class name, " +
                        className,
                    exc);
            }
            factories.put(className, factory);
        }
        return factory;
    }

    /** Hide the constructor to enforce the singleton pattern */
    private AbstractDictionaryReferenceFactory() {
        factories = new HashMap<>();
    }
}
