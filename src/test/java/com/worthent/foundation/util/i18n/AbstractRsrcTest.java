package com.worthent.foundation.util.i18n;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Base class for localization resource tests.
 *
 * @author Erik K. Worth
 */
public abstract class AbstractRsrcTest {

    private final MessageLocalizer msgLocalizer;
    private final Class<?> rsrcKeyClass;

    protected AbstractRsrcTest(
        final MessageLocalizer msgLocalizer,
        final Class<?> rsrcKeyClass) {
        this.msgLocalizer = msgLocalizer;
        this.rsrcKeyClass = rsrcKeyClass;
    }

    protected abstract ResourceKey getResourceKey(final Field fld)
        throws Exception;

    private ResourceKey checkedGetResourceKey(final Field fld) {
        try {
            return getResourceKey(fld);
        } catch (final Exception exc) {
            throw new RuntimeException("Error getting resource key", exc);
        }
    }

    /**
     * Makes sure all of the ResourceKey constants have a corresponding property
     * in the default resource bundle and the keys in the resource bundle have
     * corresponding declarations in the ResourceKey class.
     */
    protected void testRsrc(final Locale locale) {

        // Gather up the set of all the resource keys declared as constants
        final Field[] fields = rsrcKeyClass.getDeclaredFields();
        final Set<ResourceKey> declaredKeys = Arrays.stream(fields)
                .filter(fld -> ResourceKey.class.isAssignableFrom(fld.getType()))
                .filter(fld -> Modifier.isStatic(fld.getModifiers()))
                .filter(fld -> Modifier.isFinal(fld.getModifiers()))
                .map(this::checkedGetResourceKey)
                .collect(Collectors.toSet());

        // Lookup all the keys in the resource bundle and make sure there is
        // a ResourceKey constant declared for each.
        final ResourceBundle bundle = msgLocalizer.getResourceBundle(locale);
        assertThat(bundle)
                .as("No resource bundle exists for the message localizer at path, '" +
                        msgLocalizer.getResourceBundleName() + "'")
                .isNotNull();
        final String rsrcBundleName = msgLocalizer.getResourceBundleName();
        final Set<String> allKeys = new HashSet<>();
        for (Enumeration<String> e = bundle.getKeys(); e.hasMoreElements();) {
            final String key = e.nextElement();
            final String expected = bundle.getString(key);
            final ResourceKey[] keys = AbstractResourceKey.getResourceKeys(key);
            final Set<ResourceKey> keySet = (null == keys)
                    ? new HashSet<>()
                    : Arrays.stream(keys)
                    .filter(rk -> Objects.equals(rsrcBundleName, rk.getResourceBundleName()))
                    .collect(Collectors.toSet());
            assertThat(keySet.size()).as("Resource keys for the key, '" + key + "'").isEqualTo(1);
            final String actual = keySet.iterator().next().localize(locale);
            assertThat(actual).as(key).isEqualTo(expected);
            allKeys.add(key);
        }

        // Make sure all the ResourceKeys have a definition in the default resource bundle
        assertThat(allKeys).containsAll(declaredKeys.stream().map(ResourceKey::toString).collect(Collectors.toSet()));
    }
}
