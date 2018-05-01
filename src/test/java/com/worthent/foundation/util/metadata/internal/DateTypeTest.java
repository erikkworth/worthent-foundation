package com.worthent.foundation.util.metadata.internal;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

import com.worthent.foundation.util.metadata.DataSetter;
import com.worthent.foundation.util.metadata.DataType;
import com.worthent.foundation.util.metadata.MetadataFactory;
import com.worthent.foundation.util.metadata.TypeDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit Tests for Date Metadata.
 * 
 * @author Erik K. Worth
 */
public class DateTypeTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateTypeTest.class);

    /** Locale used for testing */
    private static final Locale LOCALE = Locale.US;

    /** Time zone used for testing dates */
    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("-7:00");

    /** Data Type Dictionary used to get default type definitions */
    private static final TypeDictionary DICT =
        MetadataFactory.getDefaultDictionary();

    /**
     * Test the date metadata
     */
    @Test
    public void testDate() {
        final Calendar cal_1 = Calendar.getInstance(TIME_ZONE, LOCALE);
        cal_1.set(2009, Calendar.APRIL, 1, 11, 15, 0);
        cal_1.set(Calendar.MILLISECOND, 123);
        final Date date_1 = cal_1.getTime();
        
        final DataType dateType = DICT.getType(TypeDictionary.DATE);
        final DataSetter dateValue_1 = dateType.newValue();
        dateValue_1.set(date_1);
        final String strValue = dateValue_1.getAsString();
        LOGGER.debug("Date String: {}", strValue);
        final DataSetter dateValue_2 = dateType.newValue();
        dateValue_2.setFromString(strValue);
        final Date date_2 = dateValue_2.get();
        assertThat(date_1).isEqualTo(date_2);
    }

    /**
     * Test deep copy of the date metadata type.
     */
    @Test
    public void testDateDeepcopy() {
        final Calendar cal = Calendar.getInstance(TIME_ZONE, LOCALE);
        cal.set(2009, Calendar.APRIL, 1, 11, 15, 0);
        cal.set(Calendar.MILLISECOND, 123);
        final Date date = cal.getTime();

        final DataType dateType = DICT.getType(TypeDictionary.DATE);
        final DataSetter dateTypeValue = dateType.newValue();
        dateTypeValue.set(date);

        final DataType dateTypeCopy = dateType.deepCopy();
        final DataSetter dateTypeCopyValue = dateTypeCopy.newValue();
        dateTypeCopyValue.set(date);

        assertThat((Date) dateTypeValue.get()).isEqualTo((Date) dateTypeCopyValue.get());
        assertThat(dateTypeValue.getAsString()).isEqualTo(dateTypeCopyValue.getAsString());
    }
}
