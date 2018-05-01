package com.worthent.foundation.util.i18n;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the Message Localizer utility.
 *
 * @author Erik K. Worth
 */
public class MessageLocalizerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractResourceKeyTest.class);

    /** Test Resource bundle */
    private static final String RSRC_BUNDLE = "test-resource";
    private static final String TEST_KEY_ID_1 = "test_key_1";
    private static final String TEST_KEY_ID_2 = "test_key_2";
    private static final String TEST_RSRC_VALUE_1 = "test-key-1";
    private static final String TEST_RSRC_VALUE_2 = "test-key-2 with arg ";

    @Rule
    public TestWatcher watchman= new TestWatcher() {
        @Override
        public void starting(final Description description) {
            LOGGER.debug("Starting test {}", description.getMethodName());
        }
    };

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createMessageLocalizer_withNullBundleNameAndConfirmException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("bundleName must not be null");
        MessageLocalizer.createMessageLocalizer(null, MessageLocalizerTest.class.getClassLoader());
    }

    @Test
    public void createMessageLocalizer_withNullClassLoaderAndConfirmException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("classLoader must not be null");
        MessageLocalizer.createMessageLocalizer(RSRC_BUNDLE, null);
    }

    @Test
    public void createMessageLocalizer_withNullResourceBundleAndConfirmException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("resourceBundle must not be null");
        MessageLocalizer.createMessageLocalizer(null);
    }

    @Test
    public void createMessageLocalizer_withValidResourceBundleAndConfirm() {
        final MessageLocalizer messageLocalizer = MessageLocalizer.createMessageLocalizer(
                RSRC_BUNDLE, MessageLocalizerTest.class.getClassLoader());
        final ResourceBundle resourceBundle = messageLocalizer.getResourceBundle();
        final MessageLocalizer localizerFromBundle =
                MessageLocalizer.createMessageLocalizer(resourceBundle);
        assertThat(localizerFromBundle.containsKey(TEST_KEY_ID_1)).isTrue();
    }

    @Test
    public void createMessageLocalizer_withParentAndConfirm() {
        final MessageLocalizer parentLocalizer = MessageLocalizer.createMessageLocalizer(
                "parent", MessageLocalizerTest.class.getClassLoader());
        final MessageLocalizer childLocalizer = MessageLocalizer.createMessageLocalizer(
                parentLocalizer, RSRC_BUNDLE, MessageLocalizerTest.class.getClassLoader());
        assertThat(childLocalizer.containsKey(TEST_KEY_ID_1)).isTrue();
    }

    @Test
    public void getLocalizer_forExistingLocalBundleAndConfirm() {
        MessageLocalizer.setLocalizerCache(Localizer.LOCAL, new SimpleLocalizerCache());
        final MessageLocalizer messageLocalizer = MessageLocalizer.createMessageLocalizer(
                RSRC_BUNDLE, MessageLocalizerTest.class.getClassLoader());
        Localizer localizer = MessageLocalizer.getLocalizer(Localizer.LOCAL, RSRC_BUNDLE);
        assertThat(localizer).isEqualTo(messageLocalizer);
    }

    @Test
    public void getLocalizer_forNonexistentLocalBundleAndConfirm() {
        assertThat(MessageLocalizer.getLocalizer(Localizer.LOCAL, "missing-bundle")).isNull();
    }

    @Test
    public void setLocalizerCache_forExistingComponentAndVerifyReplaced() {
        MessageLocalizer.createMessageLocalizer(RSRC_BUNDLE, MessageLocalizerTest.class.getClassLoader());
        assertThat(MessageLocalizer.getLocalizer(Localizer.LOCAL, RSRC_BUNDLE)).isNotNull();

        // Replace local cache with empty one
        MessageLocalizer.setLocalizerCache(Localizer.LOCAL, new SimpleLocalizerCache());
        assertThat(MessageLocalizer.getLocalizer(Localizer.LOCAL, RSRC_BUNDLE)).isNull();
    }

    @Test
    public void getResourceBundle_forExistingPropertiesFileAndConfirm() {
        final MessageLocalizer messageLocalizer = MessageLocalizer.createMessageLocalizer(
                RSRC_BUNDLE, MessageLocalizerTest.class.getClassLoader());

        assertThat(messageLocalizer.getResourceBundle()).isNotNull();
        assertThat(messageLocalizer.getResourceBundle(Locale.ENGLISH)).isNotNull();
    }

    @Test
    public void getResourceBundle_forNonexistentPropertiesFileAndConfirm() {
        final MessageLocalizer messageLocalizer = MessageLocalizer.createMessageLocalizer(
                "invalid-bundle", MessageLocalizerTest.class.getClassLoader());

        assertThat(messageLocalizer.getResourceBundle()).isNull();
    }

    @Test
    public void getMessageLocalizerAttributes_forExistingPropertiesFileAndConfirm() {
        final MessageLocalizer messageLocalizer = MessageLocalizer.createMessageLocalizer(
                RSRC_BUNDLE, MessageLocalizerTest.class.getClassLoader());
        assertThat(messageLocalizer.getResourceBundleName()).isEqualTo(RSRC_BUNDLE);
        assertThat(messageLocalizer.getHostingComponentName()).isEqualTo(Localizer.LOCAL);
    }

    @Test
    public void containsKey_forLocalizerWithKeyAndConfirm() {
        final MessageLocalizer messageLocalizer = MessageLocalizer.createMessageLocalizer(
                RSRC_BUNDLE, MessageLocalizerTest.class.getClassLoader());
        assertThat(messageLocalizer.containsKey(TEST_KEY_ID_1)).isTrue();
    }

    @Test
    public void localize_withJustKeyAndConfirm() {
        final MessageLocalizer messageLocalizer = MessageLocalizer.createMessageLocalizer(
                RSRC_BUNDLE, MessageLocalizerTest.class.getClassLoader());
        assertThat(messageLocalizer.localize(TEST_KEY_ID_1)).isEqualTo(TEST_RSRC_VALUE_1);
    }

    @Test
    public void localize_withLocaleAndKeyAndConfirm() {
        final MessageLocalizer messageLocalizer = MessageLocalizer.createMessageLocalizer(
                RSRC_BUNDLE, MessageLocalizerTest.class.getClassLoader());
        assertThat(messageLocalizer.localize(Locale.ENGLISH, TEST_KEY_ID_1)).isEqualTo(TEST_RSRC_VALUE_1);
    }

    @Test
    public void localize_withArgAndConfirm() {
        final MessageLocalizer messageLocalizer = MessageLocalizer.createMessageLocalizer(
                RSRC_BUNDLE, MessageLocalizerTest.class.getClassLoader());
        final String arg = "test-arg";
        assertThat(messageLocalizer.localize(TEST_KEY_ID_2, arg)).isEqualTo(TEST_RSRC_VALUE_2 + arg);
    }

    @Test
    public void localize_withArgAndLocalAndConfirm() {
        final MessageLocalizer messageLocalizer = MessageLocalizer.createMessageLocalizer(
                RSRC_BUNDLE, MessageLocalizerTest.class.getClassLoader());
        final Locale locale = Locale.ENGLISH;
        final String arg = "test-arg";
        assertThat(messageLocalizer.localize(locale, TEST_KEY_ID_2, arg)).isEqualTo(TEST_RSRC_VALUE_2 + arg);
    }

    @Test
    public void localize_withMissingResourceBundleAndConfirm() {
        final MessageLocalizer messageLocalizer = MessageLocalizer.createMessageLocalizer(
                RSRC_BUNDLE, MessageLocalizerTest.class.getClassLoader());
        final String arg = "test-arg";
        final String message = MessageLocalizer.localizeMessage(
                null, RSRC_BUNDLE, messageLocalizer, TEST_KEY_ID_2, arg);
        assertThat(message).isEqualTo(TEST_KEY_ID_2 + ", " + arg);
    }

    @Test
    public void localize_withMissingKeyAndConfirm() {
        final MessageLocalizer messageLocalizer = MessageLocalizer.createMessageLocalizer(
                RSRC_BUNDLE, MessageLocalizerTest.class.getClassLoader());
        final ResourceBundle resourceBundle = messageLocalizer.getResourceBundle();
        final String missingKey = "missing-key";
        final String arg = "test-arg";
        final String message = MessageLocalizer.localizeMessage(
                resourceBundle, RSRC_BUNDLE, null, missingKey, arg);
        assertThat(message).isEqualTo(missingKey + ", " + arg);
    }

    @Test
    public void localize_withMissingKeyAndParentAndConfirm() {
        final String parentMessage = "parent-message";
        final MessageLocalizer messageLocalizer = MessageLocalizer.createMessageLocalizer(
                RSRC_BUNDLE, MessageLocalizerTest.class.getClassLoader());
        final ResourceBundle resourceBundle = messageLocalizer.getResourceBundle();
        final String missingKey = "missing-key";
        final String arg = "test-arg";
        final Localizer parent = Mockito.mock(Localizer.class);
        when(parent.localize(resourceBundle.getLocale(), missingKey, new Object[] {arg})).thenReturn(parentMessage);
        final String message = MessageLocalizer.localizeMessage(
                resourceBundle, RSRC_BUNDLE, parent, missingKey, arg);
        assertThat(message).isEqualTo(parentMessage);
    }
}
