package com.worthent.foundation.util.metadata.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.worthent.foundation.util.metadata.DataSetter;
import com.worthent.foundation.util.metadata.DataType;
import com.worthent.foundation.util.metadata.DataTypeFactory;
import com.worthent.foundation.util.metadata.MetadataException;
import com.worthent.foundation.util.metadata.MetadataFactory;
import com.worthent.foundation.util.metadata.NamedType;
import com.worthent.foundation.util.metadata.TypeDictionary;
import com.worthent.foundation.util.recorder.DataErrorRecorder;
import com.worthent.foundation.util.recorder.Message;
import com.worthent.foundation.util.recorder.RecorderFactory;

import static org.junit.Assert.fail;

/**
 * JUnit test case to test a nested struct data structure.
 * 
 * TYPE_Struct 
 *          {
 *          DateRange : TYPE_Struct
 *                    {
 *                    StartDate : TYPE_Date
 *                    EndDate : TYPE_Date
 *                    Selection : Type_Enum (ALL, RANGE)
 *                    }
 *          }
 *          
 * Setting the following values at the top level struct fails.
 * 
 * "DateRange.Selection" = "ALL"
 * "DateRange.StartDate" = "2002-02-02 12:00:00 -0700"
 * "DateRange.EndDate"   = "2002-08-02 23:59:59 -0700"
 * 
 * The value "ALL" for "DateRange.Selection" is tried as a Date value.
 *       
 * @author Erik K. Worth
 */
public class NestedStructTest {
    private static final TypeDictionary DICT =
        MetadataFactory.getDefaultDictionary();

    private static final DataTypeFactory FACTORY =
        MetadataFactory.getInstance();

    @Test
    public void testNestedStruct() {
        Map<String, String> values = getValues();
        DataSetter dataSetter = getDataSetter();
        DataErrorRecorder recorder = RecorderFactory.newDataErrorRecorder();
        dataSetter.setStringData(values, recorder);
        if (recorder.hasErrors()) {
            StringBuilder buffer = new StringBuilder(100);
            for (Message message : recorder.getErrorMessages()) {
                buffer.append(message.getMessageType()).append(" - ").append(
                    message.getText()).append("\n");
                if (message.getException() != null) {
                    buffer
                        .append("\tException: ")
                        .append(message.getException())
                        .append("\n");
                }
            }
           fail(buffer.toString());
        }
    }

    private Map<String, String> getValues() {
        Map<String, String> values = new LinkedHashMap<>(3);
        values.put("DateRange.Selection", "ALL");
        values.put("DateRange.StartDate", "2002-02-02T12:00:00.000Z");
        values.put("DateRange.EndDate", "2002-08-02T23:59:59.000Z");
        return values;
    }

    private DataSetter getDataSetter() throws MetadataException {
        DataType dateType = DICT.getType(TypeDictionary.DATE);
        DataType enumType = FACTORY.declareEnumeration(Arrays.asList("ALL", "RANGE"));

        DataType dateRangeType = FACTORY.declareStruct(Arrays.asList(
                new NamedType("Selection", enumType),
                new NamedType("StartDate", dateType),
                new NamedType("EndDate", dateType)));

        DataType struct = FACTORY.declareStruct(Collections.singletonList(
                new NamedType("DateRange", dateRangeType)));
        return struct.newValue();
    }
}
