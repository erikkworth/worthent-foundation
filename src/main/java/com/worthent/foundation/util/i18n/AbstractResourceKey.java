package com.worthent.foundation.util.i18n;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * An abstract representation of a resource in a resource bundle. Each instance
 * of ResourceKey represents an item in a resource bundle or other key/value
 * string pair.
 * <p>
 * Not only does ResourceKey uniquely identify a specific resource; it also
 * knows how to convert that resource to a <code>String</code> value, via the
 * {@link #localize()} methods.
 * <p>
 * Implement the abstract {@link #getMessageLocalizer} method to return the
 * {@link Localizer} able to localize the resource from its key value.
 * 
 * @author Erik K. Worth
 */
public abstract class AbstractResourceKey implements ResourceKey {

    /** Cache of all ResourceKey instances created in the VM */
    private static final KeyCache keyCache = new KeyCache();

    /**
     * Delimiter separating fields in the encoded resource key ID.
     */
    private static final String ID_DELIM = "#";

    //
    // Instance Variables
    //

    /** The key string */
    private final String name;

    /** The ID of the component hosting the resource bundle. */
    private final String hostingComponent;

    /** The resource bundle name */
    private final String bundleName;

    /** The unique ID */
    private final String id;

    /**
     * Constructs a key with the given name. Precondition: no two ResourceKey
     * instances may exist with the same name.
     * 
     * @param name the name of the key.
     */
    protected AbstractResourceKey(@NotNull final String name) {
        this(Localizer.LOCAL, null, name);
    }

    /**
     * Constructs a key with the given name. Precondition: no two ResourceKey
     * instances may exist with the same name.
     * 
     * @param hostingComponent the name of the component hosting the resource
     *            bundle
     * @param name the name of the key.
     */
    protected AbstractResourceKey(
        @NotNull final String hostingComponent,
        @NotNull final String name) {
        this(hostingComponent, null, name);
    }

    /**
     * Constructs a key with the given name. Precondition: no two ResourceKey
     * instances may exist with the same name.
     * 
     * @param hostingComponent the name of the component hosting the resource
     *            bundle
     * @param bundleName the full path to the resource bundle hosting the
     *            resource identified by this key
     * @param name the name of the key
     */
    protected AbstractResourceKey(
        @NotNull final String hostingComponent,
        @Nullable final String bundleName,
        @NotNull final String name) {
        this.name = checkNotNull(name, "name must not be null");
        this.hostingComponent = checkNotNull(hostingComponent, "hostingComponent must not be null");
        this.bundleName = (null == bundleName)
                ? getLocalizer().getResourceBundleName()
                : bundleName;
        id = makeId(hostingComponent, this.getResourceBundleName(), name);
        if (!(this instanceof TempResourceKey)) {
            // Do not cache temporary keys
            keyCache.putKey(this);
        }
    }

    /**
     * Clear the resource key cache.  This is visible for testing only.
     */
    static void clearResourceKeyCache() {
        keyCache.clear();
    }

    /**
     * Returns an array of ResourceKeys matching the specified name or
     * <code>null</code> if no ResourceKeys match the name. This static accessor
     * used to get previously created resource keys based on their key name.
     * While there may be only one resource key with a given name in a resource
     * bundle, since their may be multiple resource bundles in use, the name
     * could identify multiple resource keys.
     *
     * @param keyName the resource name
     * @return an array of ResourceKeys matching the specified name or
     * <code>null</code> if no ResourceKeys match the name
     */
    public static ResourceKey[] getResourceKeys(final String keyName) {
        return keyCache.getKeys(keyName);
    }

    /**
     * Returns the <code>ResourceKey</code> for the specified bundle and key or
     * <code>null</code> if it is not found.
     * 
     * @param bundleName resource bundle name
     * @param keyName resource name
     * @return the <code>ResourceKey</code> for the specified bundle and key or
     *         <code>null</code> if it is not found
     */
    public static ResourceKey getResourceKey(
        final String bundleName,
        final String keyName) {
        return keyCache.getKey(bundleName, keyName);
    }

    /**
     * Returns the name of the component hosting the resource bundle. This
     * returns the value, {@link Localizer#LOCAL} when the resource bundle is
     * assumed to be visible to the client from its class loader.
     * 
     * @return the name of the component hosting the resource bundle
     */
    @Override
    public final String getHostingComponent() {
        return hostingComponent;
    }

    /**
     * Returns the key string
     * 
     * @return a String containing the key value
     */
    @Override
    public final String toString() {
        return name;
    }

    /**
     * Overload the equals method to compare resource key IDs.
     */
    @Override
    public final boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ResourceKey)) {
            return false;
        }
        final ResourceKey that = (ResourceKey) other;
        return this.getId().equals(that.getId());
    }

    /** Overload the standard method to return the hash code of the ID. */
    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    /**
     * Return the localized value for this key using the default locale
     * 
     * @return the localized value for this key using the default locale
     */
    @Override
    public final String localize() {
        return this.getLocalizer().localize(null, this.toString(), null);
    }

    /**
     * Return the localized value for this key using the specified locale
     * 
     * @param locale the locale to use to localize this key
     * @return the localized value for this key using the specified locale
     */
    @Override
    public final String localize(final Locale locale) {
        final Localizer localizer = getLocalizer();
        return localizer.localize(locale, this.toString(), null);
    }

    /**
     * Return the localized value for this key using the default locale
     * 
     * @param args array of localization parameters
     * @return a String
     */
    @Override
    public final String localize(final Object... args) {
        return getLocalizer().localize(null, this.toString(), args);
    }

    /**
     * Return the localized value for this key using the specified locale
     * 
     * @param locale the locale to use to localize this key
     * @param args any number of localization parameters
     * @return a String
     */
    @Override
    public final String localize(final Locale locale, final Object... args) {
        return getLocalizer().localize(locale, this.toString(), args);
    }

    /**
     * Return the localized value for this key using the default locale
     * 
     * @param arg a single localization parameter
     * @return a String
     */
    @Override
    public final String localize(final Object arg) {
        Object[] args = new Object[] { arg };
        return getLocalizer().localize(null, this.toString(), args);
    }

    /**
     * @return the name of the resource bundle used to localize messages.
     */
    @Override
    public final String getResourceBundleName() {
        return bundleName;
    }

    /**
     * @return the ID for this resource key. The ID may be used to construct a
     * temporary resource key that uses the same underlying resource bundle and
     * message key.
     */
    @Override
    public final String getId() {
        return id;
    }

    /**
     * Returns a <code>ResourceKey</code> from the resource key ID. Note that
     * the returned <code>ResourceKey</code> may not be of the same underlying
     * type as the original, but it uses the same resource bundle and message
     * key. This method should only be used in cases where a message needs to be
     * localized using a resource key that has been internalized from persistent
     * storage. All other clients should reference the original derived class
     * declared as a constant.
     * 
     * @param id the identifier for the resource key obtained using the
     *            {@link #getId} method
     * @return the resource key
     * 
     * @throws IllegalArgumentException thrown when the ID is not valid
     */
    public static ResourceKey fromId(@NotNull final String id) {
        checkNotNull(id, "id must not be null");
        final String[] tokens = id.split(ID_DELIM);
        if (tokens.length < 2) {
            throw new IllegalArgumentException("ID missing delimiter, '" + ID_DELIM + "': " + id);
        }
        if (tokens.length < 3) {
            throw new IllegalArgumentException("There must be 3 fields in the resource key ID: " + id);
        }
        final String hostingComponent = tokens[0];
        final String rsrcBundle = tokens[1];
        final String name = tokens[2];
        final ResourceKey existing = Localizer.LOCAL.equals(hostingComponent)
                ? keyCache.getKey(rsrcBundle, name)
                : null;
        return (null == existing)
                ? new TempResourceKey(hostingComponent, rsrcBundle, name)
                : existing;
    }

    /**
     * Returns the MessageLocalizer used to localize this ResourceKey.
     * 
     * @return a <code>Localizer</code>
     */
    protected abstract Localizer getMessageLocalizer();

    /**
     * Returns the message localizer and throws an exception if the localizer could not be found.
     * @return the message localizer
     * @throws IllegalStateException thrown when the localizer could not be found
     */
    private Localizer getLocalizer() {
        final Localizer localizer = getMessageLocalizer();
        if (null == localizer) {
            throw new IllegalStateException(
                "Unable to retrieve a message localizer for the class, " +
                    this.getClass().getName() +
                    ", where the hosting component is '" +
                    getHostingComponent() + "' and the bundle name is '" +
                    getResourceBundleName() + "'");
        }
        return localizer;
    }

    //
    // Localizeable Interface
    //

    /**
     * Return the localized value for this key using the specified locale
     * 
     * @param locale the locale to use to localize this key
     * @return a String
     */
    @Override
    public final String toString(final Locale locale) {
        return this.localize(locale);
    }

    //
    // Helper Methods
    //

    /** Creates a unique ID for the resource key */
    static String makeId(
        final String hostingComponent,
        final String resourceBundleName,
        final String name) {
        return hostingComponent + ID_DELIM + resourceBundleName + ID_DELIM +
            name;
    }

    //
    // Helper Classes
    //

    /**
     * Temporary resource key internalized from persistent storage or a remote call
     */
    private static final class TempResourceKey extends AbstractResourceKey {

        private TempResourceKey(
            final String hostingComponent,
            final String resourceBundle,
            final String msgKey) {
            super(hostingComponent, resourceBundle, msgKey);
        }

        @Override
        protected Localizer getMessageLocalizer() {
            return MessageLocalizer.getLocalizer(
                getHostingComponent(),
                getResourceBundleName());
        }
    }

    /**
     * Maintains a cache of all resource keys constructed in the VM. While a
     * given resource key is unique within a resource bundle, the same key could
     * be represented in different resource bundles, so there is the possibility
     * of duplicate ResourceKeys for a given key string.
     */
    private static final class KeyCache {

        /** Map of ArrayLists keyed from resource key string */
        private final Map<String, List<ResourceKey>> keyTbl;

        private KeyCache() {
            keyTbl = new HashMap<>();
        }

        private synchronized void putKey(final ResourceKey key) {
            // Get the list of keys based on the key string
            final String keyStr = key.toString();
            final List<ResourceKey> list = keyTbl.computeIfAbsent(keyStr, k -> new LinkedList<>());
            if (!list.contains(key)) {
                list.add(key);
            }
        }

        private synchronized ResourceKey[] getKeys(final String name) {
            final List<ResourceKey> list = keyTbl.get(name);
            final int size = (null == list) ? 0 : list.size();
            final ResourceKey[] keys = new ResourceKey[size];
            if (size > 0) {
                list.toArray(keys);
            }
            return keys;
        }

        private synchronized ResourceKey getKey(
            final String resourceBundleName,
            final String name) {

            final List<ResourceKey> list = keyTbl.get(name);
            if (null == list) {
                return null;
            }
            final String id = makeId(Localizer.LOCAL, resourceBundleName, name);
            return list.stream().filter(key -> id.equals(key.getId())).findAny().orElse(null);
        }

        private synchronized void clear() {
            keyTbl.clear();
        }
    } // KeyCache

}
