package com.worthent.foundation.util.metadata;

/**
 * Enumerates the types of converts that can be set on a data type.
 * 
 * @author Erik K. Worth
 */
public enum ConverterType {
    /** Converter used to convert a <code>String</code> to an object */
    FROM_STRING,
    
    /** Converter used to convert an object to its <code>String</code> form */
    TO_STRING;
}
