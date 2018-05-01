package com.worthent.foundation.util.i18n;

import java.util.Locale;

/**
 * Specifies the methods supported by objects able to localize messages using a
 * resource bundle, locale and a key.
 * 
 * @author Erik K. Worth
 */
public interface Localizer {
    
    /** Indicates a localizer is assumed to be in the same VM as the caller */
    static String LOCAL = "LOCAL";

    /**
     * @return the name of the resource bundle in its dot notation form.
     */
    String getResourceBundleName();
    
    /**
     * @return the name of the component hosting the resource bundle.  This
     * method returns the value, {@link #LOCAL} when the resource bundle is
     * assumed to be in same VM.
     */
    String getHostingComponentName();

    /**
     * Localize the message based on a locale, message key into the resource
     * bundle, and an argument list. If the resource bundle associated with this
     * instance cannot localize the message, it tries to localize the message
     * using the resources associated with the parent of this instance. It
     * continues searching up the parentage hierarchy until a resource bundle is
     * found that can localize the message or the top of the hierarchy is
     * reached.
     * 
     * @param locale the locale used to localize the message
     * @param key the <code>String</code> identifying the message in the
     *        resource bundle. The identified message may have parameter place
     *        holders that a <code>MessageFormat</code> object is able to
     *        replace from an argument list.
     * @param args the list of arguments to replace any place holders in the
     *        localized message
     * @return the localized message or the unlocalized key and arguments if the
     *         key does not identify a message in the resource bundle (never
     *         null)
     */
    String localize(Locale locale, String key, Object[] args);
}
