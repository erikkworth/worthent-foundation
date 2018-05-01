package com.worthent.foundation.util.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Encapsulates the details of the resource bundle and text message.
 * 
 * @author Erik K. Worth
 */
public final class MessageLocalizer implements Localizer {

    /** Logger for this class */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageLocalizer.class);

    /**
     * Keep track of all message localizers by hosting component and resource
     * bundle name. Synchronized access to this.
     */
    private static final Map<String, LocalizerCache> LOCALIZERS = new ConcurrentHashMap<>();

    /** Use the default local when no locale is specified */
    private static final Locale LOCALE = Locale.getDefault();

    /** Used when there are no arguments */
    private static final Object[] NO_ARGS = new Object[0];

    // Register the localizer cache for local resource bundles
    static {
        LOCALIZERS.put(Localizer.LOCAL, new SimpleLocalizerCache());
    }

    /** Resource Bundles keyed by locale */
    private final Map<Locale, ResourceBundle> msgRsrcs;

    /** The parent localizer */
    private final MessageLocalizer parent;

    /** Class loader used to load resource bundles */
    private final ClassLoader classLoader;

    /** The resource bundle base name */
    private final String bundleName;

    /** Cache the set of keys */
    private Set<String> keys;

    /**
     * Returns a message localizer used to localize exception messages. It is
     * based on a resource bundle properties file. The name of the resource
     * bundle should follow this pattern:
     * <p>
     * <i>[package prefix]</i><b>.</b><i>[bundle name]</i>
     * 
     * where <i>[package prefix]</i> is the dot separated package name
     * indicating the location of the resource bundle in the classpath, and
     * <i>[bundle name]</i> is the name of the resource bundle <b>without</b>
     * the language/country code or <code>.properties</code> suffix. For
     * example, a package called com.xyzcorp.event contains a default (US eng)
     * resource bundle property file named event.properties. The string you
     * specified in the constructor is: <b>com.xyzcorp.event.event</b>. Even if
     * you create a localized version of the file for the German language (de)
     * in Switzerland (CH) with a file named event_de_CH.properties, you still
     * specified the same string in the constructor.
     * <p>
     * The search classpath is from the perspective of the class loader
     * specified here. Specify the class loader from a class with the same
     * package prefix as the <code>resourceName</code> to make sure the resource
     * bundle can be properly loaded.
     * 
     * @param bundleName the name of the resource bundle
     * @param classLoader the class loader to use when reading the resource
     *        bundle
     * @return the object able to localize exception messages or null if the a
     *         resource bundle cannot be loaded based on the specified name and
     *         class loader
     */
    @NotNull
    public static MessageLocalizer createMessageLocalizer(
        @NotNull final String bundleName,
        @NotNull final ClassLoader classLoader) {
        return createMessageLocalizer(null, bundleName, classLoader);
    } // createMessageLocalizer

    /**
     * Returns a message localizer used to localize exception messages. It is
     * based on a resource bundle properties file. The name of the resource
     * bundle should follow this pattern:
     * <p>
     * <i>[package prefix]</i><b>.</b><i>[bundle name]</i>
     * 
     * where <i>[package prefix]</i> is the dot separated package name
     * indicating the location of the resource bundle in the classpath, and
     * <i>[bundle name]</i> is the name of the resource bundle <b>without</b>
     * the language/country code or <code>.properties</code> suffix. For
     * example, a package called com.xyzcorp.event contains a default (US eng)
     * resource bundle property file named event.properties. The string you
     * specified in the constructor is: <b>com.xyzcorp.event.event</b>. Even if
     * you create a localized version of the file for the German language (de)
     * in Switzerland (CH) with a file named event_de_CH.properties, you still
     * specify the same string in the constructor.
     * <p>
     * The search classpath is from the perspective of the class loader
     * specified here. Specify the class loader from a class with the same
     * package prefix as the <code>resourceName</code> to make sure the resource
     * bundle can be properly loaded.
     * 
     * @param parent a previously created <code>MessageLocalizer</code> that is
     *        searched when a message is not found in this one
     * @param bundleName the name of the resource bundle
     * @param classLoader the class loader to use when reading the resource
     *        bundle
     * @return the object able to localize exception messages or null if the a
     *         resource bundle cannot be loaded based on the specified name and
     *         class loader
     */
    @NotNull
    public static MessageLocalizer createMessageLocalizer(
        @Nullable final MessageLocalizer parent,
        @NotNull final String bundleName,
        @NotNull final ClassLoader classLoader) {
        return new MessageLocalizer(parent, bundleName, classLoader, null);
    } // createMessageLocalizer

    /**
     * Create a <code>MessageLocalizer</code> on the specified resource bundle.
     * This is provided for compatibility with other localization schemes.
     * 
     * @param resourceBundle the resource bundle to use
     * @return the <code>MessageLocalizer</code> on the specified resource
     *         bundle
     */
    @NotNull
    public static MessageLocalizer createMessageLocalizer(@NotNull final ResourceBundle resourceBundle) {
        checkNotNull(resourceBundle, "resourceBundle must not be null");
        final ClassLoader classLoader = resourceBundle.getClass().getClassLoader();
        return new MessageLocalizer(
                null,
                resourceBundle.getBaseBundleName(),
                (null == classLoader) ? MessageLocalizer.class.getClassLoader() : classLoader,
                resourceBundle);
    }

    /**
     * Returns an existing message localizer for the specified hosting component
     * and resource bundle name or <code>null</code> when there is no existing
     * one.
     * 
     * @param hostingComponent the identifier for the component hosting the
     *        localizer. This is a service ID when the hosting component is a
     *        service.
     * @param bundleName the fully resolved name of the resource bundle
     * @return an existing message localizer for the specified hosting component
     *         and resource bundle name or <code>null</code> when there is no
     *         existing one
     */
    @Nullable
    public static Localizer getLocalizer(
        final String hostingComponent,
        final String bundleName) {
        final LocalizerCache localizerCache = LOCALIZERS.get(hostingComponent);
        return (null == localizerCache)
                ? null
                : localizerCache.getLocalizer(bundleName);
    }

    /**
     * Register the specified localizer cache for the given hosting component.
     * This overwrites any existing cache for the specified hosting component.
     * 
     * @param hostingComponent the ID for the hosting component. For services,
     *        this is the service ID.
     * @param localizerCache the object able to cache message localizers by
     *        bundle name
     */
    public static void setLocalizerCache(
        final String hostingComponent,
        final LocalizerCache localizerCache) {
        LOCALIZERS.put(hostingComponent, localizerCache);
    }

    /**
     * Constructor is hidden to force client to use static factory method.
     */
    private MessageLocalizer(
            @Nullable final MessageLocalizer parent,
            @NotNull final String bundleName,
            @NotNull final ClassLoader classLoader,
            @Nullable final ResourceBundle initialBundle) {
        this.parent = parent;
        this.classLoader = checkNotNull(classLoader, "classLoader must not be null");
        this.bundleName = checkNotNull(bundleName, "bundleName must not be null");
        msgRsrcs = new HashMap<>();
        if (null != initialBundle) {
            msgRsrcs.put(initialBundle.getLocale(), initialBundle);
        }
        // Register this one
        final LocalizerCache localLocalizerCache = LOCALIZERS.get(Localizer.LOCAL);
        ((SimpleLocalizerCache) localLocalizerCache).putLocalizer(bundleName, this);
    }

    /**
     * @return the underlying resource bundle on the default Locale. This method
     * is provided for compatibility with other localization schemes.
     */
    public final ResourceBundle getResourceBundle() {
        return getResourceBundle(LOCALE);
    }

    /**
     * Returns the underlying resource bundle for the specified Locale or
     * <code>null</code> if no such bundle exists.
     * @param locale the desired locale
     * @return the underlying resource bundle for the specified Locale or
     * <code>null</code> if no such bundle exists
     */
    public synchronized final ResourceBundle getResourceBundle(final Locale locale) {
        ResourceBundle bundle = msgRsrcs.get(locale);
        if (null == bundle) {
            try {
                bundle = ResourceBundle.getBundle(bundleName, locale, classLoader);
            } catch (java.util.MissingResourceException err) {
                // Print out an error and keep going. This is typically a bug.
                LOGGER.error("Missing resource bundle for " + bundleName + " for locale " + locale, err);
                return null;
            }
            if (null == bundle) {
                // Print out an error and keep going. This is typically a bug.
                LOGGER.error("No resource bundle for " + bundleName + " for locale " + locale);
            } else {
                // Cache it for next time
                msgRsrcs.put(locale, bundle);
            }
        }
        return bundle;
    }

    @Override
    public final String getResourceBundleName() {
        return bundleName;
    }

    @Override
    public final String getHostingComponentName() {
        return LOCAL;
    }

    /**
     * Returns <code>true</code> when the resource bundle contains the key
     *
     * @param key the resource key
     * @return <code>true</code> when the resource bundle contains the key
     */
    public final boolean containsKey(final String key) {
        return this.getKeys().contains(key) ||
            ((null != parent) && parent.containsKey(key));
    }

    /**
     * Lookup the message based on a message key. If the resource bundle
     * associated with this instance cannot find the message, it tries to find
     * the message in the parent. If it cannot find the message anywhere, it
     * returns the key.
     * 
     * @param key the <code>String</code> identifying the message in the
     *        resource bundle. The identified message may have parameter place
     *        holders that a <code>MessageFormat</code> object is able to
     *        replace from an argument list.
     * @return the localized message or the key if message cannot be found
     */
    public String localize(final String key) {
        return this.localize(LOCALE, key, NO_ARGS);
    }

    /**
     * Lookup the message based on the specified locale and a message key. If
     * the resource bundle associated with this instance cannot find the
     * message, it tries to find the message in the parent. If it cannot find
     * the message anywhere, it returns the key.
     * 
     * @param locale the locale used to localize the message
     * @param key the <code>String</code> identifying the message in the
     *        resource bundle. The identified message may have parameter place
     *        holders that a <code>MessageFormat</code> object is able to
     *        replace from an argument list.
     * @return the localized message or the key if message cannot be found
     */
    public String localize(final Locale locale, final String key) {
        return this.localize(locale, key, NO_ARGS);
    }

    /**
     * Localize the message based on the default locale, a message key into the
     * resource bundle, and an argument list. If the resource bundle associated
     * with this instance cannot localize the message, it tries to localize the
     * message using the resources associated with the parent of this instance.
     * It continues searching up the parentage hierarchy until a resource bundle
     * is found that can localize the message or the top of the hierarchy is
     * reached.
     * 
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
    public String localize(final String key, final Object... args) {
        return this.localize(LOCALE, key, args);
    }

    @Override
    public String localize(
        final Locale locale,
        final String key,
        final Object... args) {

        // Use a valid locale
        final Locale validLocale = (null == locale) ? LOCALE : locale;

        // If the resource bundle was never found, don't localize the message
        final ResourceBundle bundle = this.getResourceBundle(validLocale);
        return localizeMessage(bundle, bundleName, parent, key, args);
    }

    /**
     * Localizes the specified key and arguments using the specified resource
     * bundle.
     * 
     * @param bundle the resource bundle containing all the resources for a
     *        given locale
     * @param bundleName the name of the resource bundle
     * @param parent the parent localizer to try if the resource is not found
     *        here (may be <code>null</code>)
     * @param key the key identifying the resource in the resource bundle
     * @param args any arguments to use to parameterize a message assuming the
     *        primary key has place holders for arguments (curly brackets
     *        enclosing an index)
     * @return the localized message for the given key and arguments using the
     *         provided resource bundle
     */
    public static String localizeMessage(
        final ResourceBundle bundle,
        final String bundleName,
        final Localizer parent,
        final String key,
        final Object... args) {
        // Make sure the key is not null. If it is null, it will generate
        // a more interesting exception.
        final String msgKey = (null == key) ? "null" : key;

        if (null == bundle) {
            return makeNonlocalizedMsg(msgKey, args);
        }
        try {
            String msg;
            try {
                // Get either the message or the message pattern
                msg = bundle.getString(msgKey);
            } catch (java.util.MissingResourceException err) {
                // cannot find key
                if (null == parent) {
                    // Best we can do
                    LOGGER.warn("Key, '{}' not found in bundle, '{}'", msgKey, bundleName, err);
                    return makeNonlocalizedMsg(msgKey, args);
                } else {
                    // Let the parent try
                    return parent.localize(bundle.getLocale(), msgKey, args);
                }
            }

            // If there are some arguments to replace the {0} place holders...
            if ((null != args) && (args.length > 0)) {
                // Use the message pattern and the arguments to produce the real
                // message
                msg = MessageFormat.format(msg, localizeArgs(bundle.getLocale(), args));
            }

            // Return the localized message
            return msg;
        } catch (Exception err) {
            LOGGER.error("Unexpected exception localizing message, {}", msgKey, err);

            // Something went wrong. Do not obscure the original exception,
            // but return a non-localized message instead
            return makeNonlocalizedMsg(msgKey, args);
        }
    } // localize

    /**
     * Returns the keys in the source bundle.
     */
    private synchronized Set<String> getKeys() {
        if (null == keys) {
            final ResourceBundle bundle = this.getResourceBundle();
            keys = bundle.keySet();
        }
        return keys;
    }

    /**
     * Returns a string message using as much of the available key and argument
     * information to construct a message string. In the case where the key is
     * not null, it is returned as the message with any arguments trailing
     * behind.
     * 
     * @param key the message key into the resource bundle
     * @param args arguments used to fill place holders in the localized message
     * @return the non-localized (or already localized) string
     */
    private static String makeNonlocalizedMsg(
        final String key,
        final Object[] args) {

        final StringBuilder msg = new StringBuilder();
        if (null != key) {
            msg.append(key);
        }
        for (int i = 0; (null != args) && (i < args.length); i++) {
            msg.append(", ");
            msg.append(args[i]);
        }
        return msg.toString();
    } // makeNonlocalizedMsg

    /** Localize arguments that we know can be localized */
    private static Object[] localizeArgs(
        final Locale locale,
        final Object[] args) {

        final int size = (null == args) ? 0 : args.length;
        final Object[] result = new Object[size];
        for (int i = 0; i < size; i++) {
            if (args[i] instanceof Localizeable) {
                result[i] = ((Localizeable) args[i]).toString(locale);
            } else {
                // No conversion
                result[i] = args[i];
            }
        } // for each argument
        return result;
    }
} // MessageLocalizer
