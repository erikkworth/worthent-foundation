package com.worthent.foundation.util.i18n;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the Abstract Resource Key.
 *
 * @author Erik K. Worth
 */
public class AbstractResourceKeyTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractResourceKeyTest.class);

    /** Identifies a remote resource key hosted by some service */
    private static final String REMOTE_COMPONENT_ID = "remote-service";
    private static final String REMOTE_KEY_ID = "remote-key";

    private static final String TEST_KEY_ID_1 = "test_key_1";
    private static final String TEST_KEY_ID_2 = "test_key_2";
    private static final String TEST_RSRC_VALUE_1 = "test-key-1";
    private static final String TEST_RSRC_VALUE_2 = "test-key-2 with arg ";

    private static class TestRsrcKey extends AbstractResourceKey {

        /** Test Resource bundle */
        private static final String RSRC_BUNDLE = "test-resource";

        /** Localizes message for the tests. */
        static final MessageLocalizer LOCALIZER = MessageLocalizer.createMessageLocalizer(
                        RSRC_BUNDLE, TestRsrcKey.class.getClassLoader());

        private TestRsrcKey(final String key) {
            super(key);
        }

        private TestRsrcKey(final String hostingComponent, final String key) {
            super(hostingComponent, key);
        }

        @Override
        protected Localizer getMessageLocalizer() {
            return LOCALIZER;
        }
    }

    private ResourceKey testRsrcKey1;
    private ResourceKey testRsrcKey2;
    private ResourceKey testRemoteKey;

    @Before
    public void setup() {
        AbstractResourceKey.clearResourceKeyCache();
        testRsrcKey1 = new TestRsrcKey(TEST_KEY_ID_1);
        testRsrcKey2 = new TestRsrcKey(TEST_KEY_ID_2);
        testRemoteKey = new TestRsrcKey(REMOTE_COMPONENT_ID, REMOTE_KEY_ID);
    }

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
    public void construct_withNullNameAndConfirmException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name must not be null");
        new TestRsrcKey(null);
    }

    @Test
    public void construct_withNullHostingComponentAndConfirmException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("hostingComponent must not be null");
        new TestRsrcKey(null, TEST_KEY_ID_1);
    }

    @Test
    public void getResourceKeys_withValidKeyNameAndConfirmSuccess() {
        final ResourceKey[] keys = TestRsrcKey.getResourceKeys(TEST_KEY_ID_1);
        assertThat(keys).isNotNull();
        assertThat(keys).containsOnly(testRsrcKey1);
    }

    @Test
    public void getResourceKeys_withInvalidKeyNameAndConfirm() {
        final ResourceKey[] keys = TestRsrcKey.getResourceKeys("invalid-key");
        assertThat(keys).isNotNull();
        assertThat(keys).isEmpty();
    }

    @Test
    public void getResourceKey_withValidKeyAndConfirm() {
        final ResourceKey key = TestRsrcKey.getResourceKey(TestRsrcKey.RSRC_BUNDLE, TEST_KEY_ID_1);
        assertThat(key).isEqualTo(testRsrcKey1);
    }

    @Test
    public void getResourceKey_withInvalidKeyAndConfirm() {
        final ResourceKey key = TestRsrcKey.getResourceKey(TestRsrcKey.RSRC_BUNDLE, "invalid-key");
        assertThat(key).isNull();
    }

    @Test
    public void getHostingComponent_andConfirmLocal() {
        assertThat(testRsrcKey1.getHostingComponent()).isEqualTo(Localizer.LOCAL);
    }

    @Test
    public void toString_withNoLocalAndConfirm() {
        assertThat(testRsrcKey1.toString()).isEqualTo(TEST_KEY_ID_1);
    }

    @Test
    public void toString_withLocalAndConfirm() {
        assertThat(testRsrcKey1.toString(Locale.ENGLISH)).isEqualTo(TEST_RSRC_VALUE_1);
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void equals_withSelfAndConfirmTrue() {
        assertThat(testRsrcKey1.equals(testRsrcKey1)).isTrue();
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Test
    public void equals_withWrongTypeAndConfirmFalse() {
        assertThat(testRsrcKey1.equals("Not a Resource Key")).isFalse();
    }

    @Test
    public void equals_withDifferentKeyAndConfirmFalse() {
        assertThat(testRsrcKey1.equals(testRsrcKey2)).isFalse();
    }

    @Test
    public void localize_withoutArgsAndConfirm() {
        assertThat(testRsrcKey1.localize()).isEqualTo(TEST_RSRC_VALUE_1);
    }

    @Test
    public void localize_withArgAndConfirm() {
        final String arg = "test-arg";
        assertThat(testRsrcKey2.localize(arg)).isEqualTo(TEST_RSRC_VALUE_2 + arg);
    }

    @Test
    public void localize_withArgAndLocalAndConfirm() {
        final Locale locale = Locale.ENGLISH;
        final String arg = "test-arg";
        assertThat(testRsrcKey2.localize(locale, arg)).isEqualTo(TEST_RSRC_VALUE_2 + arg);
    }

    @Test
    public void localize_withInvalidResourceBundleName() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Unable to retrieve a message localizer");
        final ResourceKey keyForInvalidBundle = new AbstractResourceKey("badKey") {
            @Override
            protected Localizer getMessageLocalizer() {
                // No message localizer
                return null;
            }
        };
        keyForInvalidBundle.localize();
    }

    @Test
    public void getResourceBundleName_andConfirm() {
        assertThat(testRsrcKey1.getResourceBundleName()).isEqualTo(TestRsrcKey.RSRC_BUNDLE);
    }

    @Test
    public void fromId_withValidIdAndConfirm() {
        final String id = testRsrcKey1.getId();
        assertThat(TestRsrcKey.fromId(id)).isEqualTo(testRsrcKey1);
    }

    @Test
    public void fromId_withTooFewTokensAndConfirmFailure() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("ID missing delimiter");
        assertThat(TestRsrcKey.fromId("no-delimiters"));
    }

    @Test
    public void fromId_withTooManyTokensAndConfirmFailure() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("There must be 3 fields in the resource key ID");
        assertThat(TestRsrcKey.fromId("#invalid"));
    }

    @Test
    public void fromId_withRemoteResourceKeyAndConfirm() {
        final String remoteId = testRemoteKey.getId();
        final ResourceKey resourceKey = TestRsrcKey.fromId(remoteId);
        assertThat(resourceKey).isNotNull();
        assertThat(resourceKey.getHostingComponent()).isEqualTo(REMOTE_COMPONENT_ID);
    }

}
