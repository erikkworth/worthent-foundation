package com.worthent.foundation.util.lang;

/**
 * Basic string utilities to avoid pulling in external dependencies.
 *
 * @author Erik K. Worth
 */
public class StringUtils {

    /**
     * Returns <code>true</code> when the provided string is <code>null</code> or empty.
     *
     * @param str the string to test
     * @return <code>true</code> when the provided string is <code>null</code> or empty
     */
    public static boolean isBlank(final String str) {
        return ((null == str) || str.length() == 0);
    }

    /**
     * Returns <code>true</code> when the provided string is neither <code>null</code> nor empty.
     *
     * @param str the string to test
     * @return <code>true</code> when the provided string is neither <code>null</code> nor empty
     */
    public static boolean isNotBlank(final String str) {
        return ((null != str) && str.length() > 0);
    }

    /**
     * Returns a string without leading or trailing white space and a <code>null</code> value if the string is blank.
     *
     * @param str the string to trim
     * @return a string without leading or trailing white space and a <code>null</code> value if the string is blank
     */
    public static String trimToNull(final String str) {
        if (null == str || (str.length() == 0)) {
            return null;
        }
        int start = 0;
        int finish = str.length() - 1;
        while (Character.isWhitespace(str.charAt(start)) && (start < finish)) {
            start++;
        }
        while (Character.isWhitespace(str.charAt(finish)) && (finish >= start)) {
            finish--;
        }
        return (finish == start) && Character.isWhitespace(str.charAt(start))
                ? null
                : str.substring(start, finish + 1);
    }

}
