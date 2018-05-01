package com.worthent.foundation.util.metadata.internal;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.worthent.foundation.util.metadata.DataSetter;
import com.worthent.foundation.util.metadata.DataType;
import com.worthent.foundation.util.metadata.DataTypeFactory;
import com.worthent.foundation.util.metadata.MetadataFactory;
import com.worthent.foundation.util.metadata.NamedType;
import com.worthent.foundation.util.metadata.TypeDictionary;
import com.worthent.foundation.util.recorder.DataErrorRecorder;
import com.worthent.foundation.util.recorder.RecorderFactory;

/**
 * Tests for the list data type.
 * 
 * @author Erik K. Worth
 */
public class ListTest {

    /** Locale used for testing */
    private static final Locale LOCALE = Locale.US;

    /** Time zone used for testing dates */
    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("-7:00");

    /** Data Type Factory used to create data types */
    private static final DataTypeFactory FACTORY =
        MetadataFactory.getInstance();

    /** Data Type Dictionary used to get default type definitions */
    private static final TypeDictionary DICT =
        MetadataFactory.getDefaultDictionary();

    private static final DataType STRING_TYPE = DICT.getType(TypeDictionary.STRING);
    private static final DataType INTEGER_TYPE = DICT.getType(TypeDictionary.INTEGER);
    private static final DataType MAP_TYPE = FACTORY.declareMap(LinkedHashMap.class.getName(), STRING_TYPE);
    private static final DataType LIST_OF_MAPS_TYPE = FACTORY.declareList(LinkedList.class.getName(), MAP_TYPE);

    private static final DataType LIST_OF_STRING_TYPE = FACTORY.declareList(LinkedList.class.getName(), STRING_TYPE);

    private static final DataType LIST_OF_INTEGER_TYPE = FACTORY.declareList(LinkedList.class.getName(), INTEGER_TYPE);

    /** Struct field names used for testing */
    private static final String START_DATE = "StartDate";
    private static final String END_DATE = "EndDate";
    private static final String REPOS = "Repos";
    private static final String TEXT_KEYWORDS = "TextKeywords";
    private static final String CUSTODIANS = "Custodians";
    private static final String SELECTION = "Selection";
    private static final String OPERATION = "Operation";
    private static final String EMAIL_PATTERNS = "EmailPatterns";

    /** The legal values for the SELECTION field */
    private static final String SELECTION_SENDER = "SENDER";
    private static final String[] SELECTION_OPTIONS = { SELECTION_SENDER, "RECIPIENT", "ALL" };

    /** Data Type for SELECTION field */
    private static final DataType SELECTION_TYPE = FACTORY.declareEnumeration(Arrays.asList(SELECTION_OPTIONS));

    /** The legal values for the OPERATION field */
    private static final String OPERATION_EQUALS = "EQUALS";
    private static final String[] OPERATION_OPTIONS = { OPERATION_EQUALS, "NOT_EQUALS" };

    /** Data Type for OPERATION field */
    private static final DataType OPERATION_TYPE = FACTORY.declareEnumeration(Arrays.asList(OPERATION_OPTIONS));

    /** Custodian Selection Search Criteria Structure */
    private static final NamedType[] CUSTODIAN_FIELDS = {
            new NamedType(SELECTION, SELECTION_TYPE),
            new NamedType(OPERATION, OPERATION_TYPE),
            new NamedType(EMAIL_PATTERNS, DICT.getType(TypeDictionary.STRING)) };

    private static final DataType CUSTODIAN_SELECTOR = FACTORY.declareStruct(Arrays.asList(CUSTODIAN_FIELDS));

    /** List of Custodian Selection Search Criteria */
    private static final DataType CUSTODIAN_SELECTORS = FACTORY.declareList(ArrayList.class.getName(), CUSTODIAN_SELECTOR);

    /** Repository Search Criteria Structure */
    private static final NamedType[] REPO_CRITERIA_FIELDS = {
            new NamedType(TEXT_KEYWORDS, DICT.getType(TypeDictionary.STRING)),
            new NamedType(CUSTODIANS, CUSTODIAN_SELECTORS) };

    private static final DataType REPO_CRITERIA = FACTORY.declareStruct(Arrays.asList(REPO_CRITERIA_FIELDS));

    private static final DataType REPO_MAP = FACTORY.declareMap(HashMap.class.getName(), REPO_CRITERIA);

    /** Test Keys for repository map entries */
    private static final String BEDROCK_EMAIL_REPO = "bedrockEmail";
    private static final String BEDROCK_EMAIL_PATH = REPOS + "." + BEDROCK_EMAIL_REPO;
    private static final String BEDROCK_EMAIL_KEYWORDS_PATH = BEDROCK_EMAIL_PATH + "." + TEXT_KEYWORDS;
    private static final String BEDROCK_EMAIL_CUSTODIANS_PATH = BEDROCK_EMAIL_PATH + "." + CUSTODIANS;

    /** Top-level Search Criteria Structure */
    private static final NamedType[] SEARCH_FIELDS =
        {
            new NamedType(START_DATE, DICT.getType(TypeDictionary.DATE)),
            new NamedType(END_DATE, DICT.getType(TypeDictionary.DATE)),
            new NamedType(REPOS, REPO_MAP) };

    private static final DataType SEARCH_CRITERIA =
        FACTORY.declareStruct(Arrays.asList(SEARCH_FIELDS));

    @Test
    public void testList() {
        // Test Dates
        final Calendar date_1 = Calendar.getInstance(TIME_ZONE, LOCALE);
        final Calendar date_2 = Calendar.getInstance(TIME_ZONE, LOCALE);
        date_1.set(2009, Calendar.APRIL, 1, 11, 15);
        date_2.set(2009, Calendar.JULY, 1, 0, 0);

        // Create the empty search criteria object
        final DataSetter searchCriteria = SEARCH_CRITERIA.newValue();

        // Set dates
        searchCriteria.set(START_DATE, date_1.getTime());
        searchCriteria.set(END_DATE, date_2.getTime());

        // Create repository search criteria for bedrock email and add it
        // to the map
        final DataSetter bedrockEmail = REPO_CRITERIA.newValue();
        searchCriteria.set(BEDROCK_EMAIL_PATH, bedrockEmail);

        // Add keyword search criteria
        searchCriteria.set(BEDROCK_EMAIL_KEYWORDS_PATH, "aig executive bonus");

        // Add Custodian search criteria filter for sender
        final DataSetter senderFilter = CUSTODIAN_SELECTOR.newValue();
        senderFilter.set(SELECTION, SELECTION_SENDER);
        senderFilter.set(OPERATION, OPERATION_EQUALS);
        senderFilter.set(
            EMAIL_PATTERNS,
            "bdude@stratify.com;bogus.dude@stratify.com");
        searchCriteria.addElement(BEDROCK_EMAIL_CUSTODIANS_PATH, senderFilter);

        // Retrieve one of the leaf items in the structure
        final String path = BEDROCK_EMAIL_CUSTODIANS_PATH + "[0]" + OPERATION;
        final String actual = searchCriteria.get(path);
        assertTrue("Expected operation to be " +
            OPERATION_EQUALS +
            " but found instead '" +
            actual +
            "'", OPERATION_EQUALS.equals(actual));

        // Create a copy
        final DataSetter copy = searchCriteria.getDeepCopy();

        // Make sure the leaf value is still correct in the copy
        final String actualCopy = copy.get(path);
        assertTrue("Expected operation to be " +
            OPERATION_EQUALS +
            " but found instead '" +
            actual +
            "'", OPERATION_EQUALS.equals(actualCopy));
    }

    /**
     * Test adding an element to a list when the list is the top-level data
     * object.
     */
    @Test
    public void testListAddElement() {
        final DataSetter dataSetter = LIST_OF_MAPS_TYPE.newValue();
        dataSetter.addElement(null, MAP_TYPE.newValue());
        dataSetter.set("[0]Test", "TestValue");
    }

    /** Test setting the value of an element in a list */
    @Test
    public void testListSet() {
        final String expected = "Index Two";
        final DataSetter dataSetter = LIST_OF_STRING_TYPE.newValue();
        dataSetter.set("[2]", expected);
        final String actual = dataSetter.get("[2]");
        assertTrue("Expected '" +
            expected +
            "', but found instead, '" +
            actual +
            "'", expected.equals(actual));
        final int size = dataSetter.size();
        // It should have extended the list so that it could set the element
        // at index 2
        assertTrue(
            "Expected list to be of size, 3, but found it to be " + size,
            3 == size);
    }

    /** Test setting the value of elements in a list through string values map. */
    @Test
    public void testListSetForPrimitiveElementType() {
        final Integer expected = 110;
        final Map<String, String> stringValues = new LinkedHashMap<>();
        stringValues.put("[1]", "10");
        stringValues.put("[5]", "50");
        stringValues.put("[11]", expected.toString());
        DataErrorRecorder errRecorder = RecorderFactory.newDataErrorRecorder();
        final DataSetter dataSetter =
            LIST_OF_INTEGER_TYPE.newValue().setStringData(
                stringValues,
                errRecorder);
        if (errRecorder.hasErrors()) {
            fail(errRecorder.toString());
        }
        final Integer actual = dataSetter.get("[11]");
        assertTrue("Expected '" +
            expected +
            "', but found instead, '" +
            actual +
            "'", expected.equals(actual));
    }

    /** Test setting the value of an element in a list from string data */
    @Test
    public void testListSetFromStringData() {
        final String expected = "Index Two";
        final DataSetter dataSetter = LIST_OF_MAPS_TYPE.newValue();
        final Map<String, String> strData = new HashMap<>();
        strData.put("[2]Two", expected);
        final DataErrorRecorder errRecorder = RecorderFactory.newDataErrorRecorder();
        final DataSetter result =
            dataSetter.setStringData(strData, errRecorder);
        assertTrue(
            "Error setting list element: " + errRecorder.toString(),
            !errRecorder.hasErrors());
        final String actual = result.get("[2]Two");
        assertTrue("Expected '" +
            expected +
            "', but found instead, '" +
            actual +
            "'", expected.equals(actual));
        final int size = result.size();
        // It should have extended the list so that it could set the element
        // at index 2
        assertTrue(
            "Expected list to be of size, 3, but found it to be " + size,
            3 == size);
    }
}
