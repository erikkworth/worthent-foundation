package com.worthent.foundation.util.i18n;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a simple map-based cache for message localizers.
 * 
 * @author Erik K. Worth
 */
public class SimpleLocalizerCache implements LocalizerCache {

    /** Map of message localizers by bundle name */
    private final Map<String, Localizer> localizers;

    public SimpleLocalizerCache() {
        localizers = new HashMap<>();
    }

    @Override
    public Localizer getLocalizer(final String bundleName) {
        return localizers.get(bundleName);
    }

    /**
     * Puts the specified localizer into the cache keyed by the specified bundle
     * name if one does not already exist by that name
     * 
     * @param bundleName the name of the resource bundle
     * @param localizer the object able to localize messages
     */
    public void putLocalizer(final String bundleName, final Localizer localizer) {
        if (!localizers.containsKey(bundleName)) {
            localizers.put(bundleName, localizer);
        }
    }

}
