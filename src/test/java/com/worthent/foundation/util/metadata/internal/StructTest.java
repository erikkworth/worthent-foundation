package com.worthent.foundation.util.metadata.internal;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import org.junit.Test;

import com.worthent.foundation.util.metadata.DataSetter;
import com.worthent.foundation.util.metadata.DataType;
import com.worthent.foundation.util.metadata.DataTypeFactory;
import com.worthent.foundation.util.metadata.MetadataFactory;
import com.worthent.foundation.util.metadata.NamedType;
import com.worthent.foundation.util.metadata.TypeDictionary;

/**
 * Test suite for metadata implementation classes.
 * 
 * @author Erik K. Worth
 */
public class StructTest {

    /** Locale used for testing */
    private static final Locale LOCALE = Locale.US;

    /** Time zone used for testing dates */
    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("-7:00");

    /** Data Type Factory used to create data types */
    private static final DataTypeFactory FACTORY = MetadataFactory.getInstance();

    /** Data Type Dictionary used to get default type definitions */
    private static final TypeDictionary DICT =
        MetadataFactory.getDefaultDictionary();

    /** Struct field names used for testing */
    private static final String START_DATE = "StartDate";
    private static final String END_DATE = "EndDate";

    /** Test Structure */
    private static final NamedType[] FIELD_SET_1 = {
            new NamedType(START_DATE, DICT.getType(TypeDictionary.DATE)),
            new NamedType(END_DATE, DICT.getType(TypeDictionary.DATE)) };

    private static final NamedType[] FIELD_SET_2 = {
            new NamedType(START_DATE, DICT.getType(TypeDictionary.DATE)),
            new NamedType(END_DATE, DICT.getType(TypeDictionary.DATE)) };

    /** Struct field names */
    private static final String[] FIELD_NAMES_1 = { START_DATE, END_DATE };

    @Test
    public void testStruct() {
        // Test Dates
        final Calendar date_1 = Calendar.getInstance(TIME_ZONE, LOCALE);
        final Calendar date_2 = Calendar.getInstance(TIME_ZONE, LOCALE);
        date_1.set(2009, Calendar.APRIL, 1, 11, 15);
        date_2.set(2009, Calendar.JULY, 1, 0, 0);

        // Define the struct type
        final List<NamedType> fields = Arrays.asList(FIELD_SET_1);
        final DataType structDef = FACTORY.declareStruct(fields);

        // Define another structure that should be the same
        final DataType sameStructDef =
            FACTORY.declareStruct(Arrays.asList(FIELD_SET_2));
        assertTrue(
            "The structures are not the same", 
            structDef.equals(sameStructDef));

        // Create an instance and populate it with with field values
        final DataSetter struct = structDef.newValue();
        struct.set(START_DATE, date_1.getTime());
        struct.set(END_DATE, date_2.getTime());

        // Verify the structure has the expected fields.
        final Set<String> expectedFields = new HashSet<>(Arrays.asList(FIELD_NAMES_1));
        final Set<String> actualFields = new HashSet<>(struct.getMapElementNames());
        assertTrue("Expected fields, " +
            expectedFields +
            ", but found instead, " +
            actualFields, expectedFields.equals(actualFields));

        // Get the fields and verify they are the same as what was set
        final Date actualDate1 = struct.get(START_DATE);
        assertTrue("Expected date, " +
            date_1.getTime() +
            ", but found instead date, " +
            actualDate1, date_1.getTime().equals(actualDate1));
        final Date actualDate2 = struct.get(END_DATE);
        assertTrue("Expected date, " +
            date_2.getTime() +
            ", but found instead date, " +
            actualDate2, date_2.getTime().equals(actualDate2));

        // Copy the structure and verify it is the same
        final DataSetter copy = struct.getDeepCopy();
        final Set<String> copyFields = new HashSet<>(copy.getMapElementNames());
        assertTrue("Expected fields, " +
            expectedFields +
            ", but found instead, " +
            copyFields, expectedFields.equals(copyFields));
        final Date copyDate1 = copy.get(START_DATE);
        assertTrue("Expected date, " +
            date_1.getTime() +
            ", but found instead date, " +
            copyDate1, date_1.getTime().equals(copyDate1));
        final Date copyDate2 = copy.get(END_DATE);
        assertTrue("Expected date, " +
            date_2.getTime() +
            ", but found instead date, " +
            copyDate2, date_2.getTime().equals(copyDate2));

        // Modify the copy and make sure the original does not change
        copy.set(START_DATE, date_2.getTime());
        final Date origDate1 = struct.get(START_DATE);
        assertTrue("Expected date, " +
            date_1.getTime() +
            ", but found instead date, " +
            origDate1, date_1.getTime().equals(origDate1));
    }
}
