package com.worthent.foundation.util.metadata.internal;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.worthent.foundation.util.metadata.DataSetter;
import com.worthent.foundation.util.metadata.DataType;
import com.worthent.foundation.util.metadata.DataTypeFactory;
import com.worthent.foundation.util.metadata.MetadataFactory;
import com.worthent.foundation.util.metadata.NamedType;
import com.worthent.foundation.util.metadata.TypeDictionary;

/**
 * Test cases for the Reference Type.
 * 
 * @author Erik K. Worth
 */
public class RefTypeTest {

    /** Data Type Factory used to create data types */
    private static final DataTypeFactory FACTORY = MetadataFactory.getInstance();

    /** Data Type Dictionary used to get default type definitions */
    private static final TypeDictionary DICT = MetadataFactory.getDefaultDictionary();

    /** ID for the base dictionary */
    private static final String BASE_DICT_ID = "BaseDictId";

    /** Base Type Dictionary */
    private static final TypeDictionary BASE_DICT = DICT.newDictionary(BASE_DICT_ID);

    /** The Type ID for the test reference type */
    private static final String FORWARD_REFERENCE = "ForwardReference";

    /** Shortcut for the string type */
    private static final DataType STRING_TYPE = DICT.getType(TypeDictionary.STRING);

    /** Forward Reference to type in the base dictionary */
    private static final DataType FORWARD_REF_TYPE = FACTORY.declareReference(FORWARD_REFERENCE, BASE_DICT);

    /** Test fields in root struct */
    private static final String TEST_FLD_A = "TestFieldA";
    private static final String TEST_FLD_B = "TestFieldB";
    private static final String TEST_FLD_C = "TestFieldC";

    /** Test Paths at different levels */
    private static final String TEST_FLD_A_LEVEL_0 = TEST_FLD_A;
    private static final String TEST_FLD_A_LEVEL_1 = TEST_FLD_B + '.' + TEST_FLD_A;
    private static final String TEST_FLD_C_LEVEL_1 = TEST_FLD_B + '.' + TEST_FLD_C;

    /** Test Data Values */
    private static final String FLD_A_VALUE_LEVEL_0 = "Level-0 Field A Value";
    private static final String FLD_A_VALUE_LEVEL_1 = "Level-1 Field A Value";
    private static final String FLD_C_VALUE_LEVEL_1 = "Level-1 Field C Value";

    /** Structure fields where field B is a reference */
    private static final NamedType[] RECURSIVE_STRUCT_FIELDS =
        new NamedType[] {
            new NamedType(TEST_FLD_A, STRING_TYPE),
            new NamedType(TEST_FLD_B, FORWARD_REF_TYPE) };

    /** Structure that has a field with a reference */
    private static final DataType RECURSIVE_STRUCT_TYPE =
        FACTORY.declareStruct(RECURSIVE_STRUCT_FIELDS);

    /** Simple flat struct fields */
    private static final NamedType[] FLAT_STRUCT_FIELDS =
        new NamedType[] {
            new NamedType(TEST_FLD_A, STRING_TYPE),
            new NamedType(TEST_FLD_C, STRING_TYPE) };
    
    /** Simple flat struct type */
    private static final DataType FLAT_STRUCT_TYPE = FACTORY.declareStruct(FLAT_STRUCT_FIELDS);

    static {
        // Make the reference refer to the struct that has a reference field
        BASE_DICT.putType(FORWARD_REFERENCE, RECURSIVE_STRUCT_TYPE);
    }

    @Test
    public void testRecursiveReference() {
        // Create an instance of the recursive data structure
        final DataSetter dataObject = RECURSIVE_STRUCT_TYPE.newValue();
        dataObject.set(TEST_FLD_A_LEVEL_0, FLD_A_VALUE_LEVEL_0);
        dataObject.set(TEST_FLD_A_LEVEL_1, FLD_A_VALUE_LEVEL_1);
        final DataSetter copy = dataObject.getDeepCopy();
        final String level_1_value = copy.get(TEST_FLD_A_LEVEL_1);
        assertTrue("Value At Level 1 is not the expected value", level_1_value
            .equals(FLD_A_VALUE_LEVEL_1));
    }

    @Test
    public void testDictionaryOverride() {
        final TypeDictionary childDict = BASE_DICT.newDictionary();
        
        // Get the type definition containing a reference in field B
        final DataType rootType = childDict.getType(FORWARD_REFERENCE);
        
        // Override the type referenced by field B
        childDict.putType(FORWARD_REFERENCE, FLAT_STRUCT_TYPE);
        
        // Now the level one struct has an a C field instead of a B field
        final DataSetter dataObject = rootType.newValue();
        dataObject.set(TEST_FLD_A_LEVEL_0, FLD_A_VALUE_LEVEL_0);
        dataObject.set(TEST_FLD_A_LEVEL_1, FLD_A_VALUE_LEVEL_1);
        dataObject.set(TEST_FLD_C_LEVEL_1, FLD_C_VALUE_LEVEL_1);
        final DataSetter copy = dataObject.getDeepCopy();
        final String level_1_A_value = copy.get(TEST_FLD_A_LEVEL_1);
        assertTrue("Value for field A at Level 1 is not the expected value", level_1_A_value
            .equals(FLD_A_VALUE_LEVEL_1));
        final String level_1_C_value = copy.get(TEST_FLD_C_LEVEL_1);
        assertTrue("Value for field C at Level 1 is not the expected value", level_1_C_value
            .equals(FLD_C_VALUE_LEVEL_1));
    }

}
