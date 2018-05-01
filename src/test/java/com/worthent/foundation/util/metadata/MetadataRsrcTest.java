package com.worthent.foundation.util.metadata;

import java.lang.reflect.Field;
import java.util.Locale;

import org.junit.Test;

import com.worthent.foundation.util.i18n.AbstractRsrcTest;
import com.worthent.foundation.util.i18n.ResourceKey;

/**
 * Tests the resources in the resource bundle.
 * 
 * @author Erik K. Worth
 */
public class MetadataRsrcTest extends AbstractRsrcTest {

    private static final Locale LOCALE = Locale.getDefault();
    
    public MetadataRsrcTest() {
        super(MetadataRsrc.LOCALIZER, MetadataRsrc.class);
    }

    @Test
    public void testMetadataRsrc() throws Exception {
        testRsrc(LOCALE);
    }

    @Override
    protected ResourceKey getResourceKey(final Field fld) throws Exception {
        return (ResourceKey) fld.get(null);
    }
}
