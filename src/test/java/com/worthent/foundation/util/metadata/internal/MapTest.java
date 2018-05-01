package com.worthent.foundation.util.metadata.internal;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

import com.worthent.foundation.util.metadata.DataSetter;
import com.worthent.foundation.util.metadata.DataType;
import com.worthent.foundation.util.metadata.DataTypeFactory;
import com.worthent.foundation.util.metadata.MetadataFactory;
import com.worthent.foundation.util.metadata.TypeDictionary;

/**
 * Test case for the Map Data Type.
 * 
 * @author Erik K. Worth
 */
public class MapTest {

    /** Data Type Factory used to create data types */
    private static final DataTypeFactory FACTORY =
        MetadataFactory.getInstance();

    /** Data Type Dictionary used to get default type definitions */
    private static final TypeDictionary DICT =
        MetadataFactory.getDefaultDictionary();

    /** String Key/Values */
    private static final String[][] KEY_VALUES =
        { { "key1", "value1" }, { "key2", "value2" } };

    @Test
    public void testStruct() {
        // Define Map of Strings
        final DataType stringType = DICT.getType(TypeDictionary.STRING);
        final DataType mapType =
            FACTORY.declareMap("java.util.HashMap", stringType);
        mapType.assertValid();

        // Create an instance and add values
        final DataSetter map = mapType.newValue();
        for (final String[] keyValue : KEY_VALUES) {
            map.set(keyValue[0], keyValue[1]);
        }
        mapType.assertValid(map.get());

        // Make sure the values are all there
        for (final String[] keyValue : KEY_VALUES) {
            final String expected = keyValue[1];
            final String actual = map.get(keyValue[0]);
            assertTrue("Expected value of '" +
                expected +
                "', for key, '" +
                keyValue[0] +
                "', but found instead, '" +
                actual, expected.equals(actual));
        }
    }
}
