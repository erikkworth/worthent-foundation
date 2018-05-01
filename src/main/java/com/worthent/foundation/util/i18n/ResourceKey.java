package com.worthent.foundation.util.i18n;

import java.io.Serializable;
import java.util.Locale;

/**
 * Specifies the operations for a resource in a resource bundle. Each instance
 * of ResourceKey represents an item in a resource bundle or other key/value
 * string pair.
 * <p>
 * Not only does ResourceKey uniquely identify a specific resource; it also
 * knows how to convert that resource to a <code>String</code> value, via the
 * {@link #localize()} methods.
 * <p>
 *
 * @author Erik K. Worth
 */
public interface ResourceKey extends Localizeable, Serializable {
    /**
     * Returns the name of the component hosting the resource bundle. This
     * returns the value, {@link Localizer#LOCAL} when the resource bundle is
     * assumed to be visible to the client from its class loader.
     * 
     * @return the name of the component hosting the resource bundle
     */
    String getHostingComponent();

    /**
     * Return the localized value for this key using the default locale
     * 
     * @return the localized value for this key using the default locale
     */
    String localize();

    /**
     * Return the localized value for this key using the specified locale
     * 
     * @param locale the locale to use to localize this key
     * @return the localized value for this key using the specified locale
     */
    String localize(Locale locale);

    /**
     * Return the localized value for this key using the default locale
     * 
     * @param args array of localization parameters
     * @return a String
     */
    String localize(Object... args);

    /**
     * Return the localized value for this key using the specified locale
     * 
     * @param locale the locale to use to localize this key
     * @param args any number of localization parameters
     * @return a String
     */
    String localize(Locale locale, Object... args);

    /**
     * Return the localized value for this key using the default locale
     * 
     * @param arg a single localization parameter
     * @return a String
     */
    String localize(Object arg);

    /**
     * @return the name of the resource bundle used to localize messages.
     */
    String getResourceBundleName();

    /**
     * @return the ID for this resource key. The ID may be used to construct a
     * temporary resource key that uses the same underlying resource bundle and
     * message key.
     */
    String getId();

}
