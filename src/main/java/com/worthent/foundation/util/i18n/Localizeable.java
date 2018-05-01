package com.worthent.foundation.util.i18n;

import java.util.Locale;

/**
 * Specifies a <code>toString(Locale)</code> method for objects that can be
 * localized.
 * 
 * @author Erik K. Worth
 */
public interface Localizeable {

    /**
     * Returns the localized String representation of the object
     * 
     * @param locale the locale used to localize the object
     * @return the localized String representation of the object
     */
    String toString(Locale locale);
}
