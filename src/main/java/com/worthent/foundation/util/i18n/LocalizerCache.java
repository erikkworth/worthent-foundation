package com.worthent.foundation.util.i18n;

/**
 * Specifies the methods used to cache implementations of the
 * <code>Localizer</code> interface by bundle name.
 * 
 * @author Erik K. Worth
 */
public interface LocalizerCache {

    /**
     * Returns an object able to localize messages given its bundle name or
     * <code>null</code> if no localizer is found by the specified name.
     * 
     * @param bundleName the name of the resource bundle
     * @return an object able to localize messages given its bundle name or
     * <code>null</code> if no localizer is found by the specified name
     */
    Localizer getLocalizer(String bundleName);
}
