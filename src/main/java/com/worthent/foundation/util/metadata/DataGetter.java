package com.worthent.foundation.util.metadata;

import java.io.Serializable;
import java.text.Format;
import java.util.Collection;
import java.util.Map;

import com.worthent.foundation.util.recorder.DataErrorRecorder;

/**
 * Specifies accessors for the children data within complex data objects. The
 * implementation operates on a parent data object type that supports nested
 * data, such as a struct, list and/or array data.
 * <p>
 * The path arguments provided may refer to nested structures. See
 * {@link DataSetter} for details on how to specify a path.
 * 
 * @author Erik K. Worth
 * @version $Id: DataGetter.java 52 2011-04-04 05:11:28Z eworth $
 */
public interface DataGetter extends Serializable {

    /**
     * @return a deep copy of the data that may be edited.
     */
    DataSetter getDeepCopy() throws MetadataException;

    /** @return the data type for this property */
    DataType getType();

    /**
     * Returns the data type at the specified path relative to this property.
     * 
     * @param path the path to the desired property type.
     * @return the data type at the specified path relative to this property
     * @throws MetadataException thrown when the path does not match a data type
     */
    DataType getType(String path) throws MetadataException;

    /**
     * Returns another data getter for the data at the specified path. The root
     * of the new data setter becomes the property at the specified path. The
     * returned data getter accesses the same copy of the underlying property
     * values as the original.
     * 
     * @param path the path to the desired property type.
     * @return another data getter for the data at the specified path
     * @throws MetadataException thrown when the path does not match a property type
     */
    DataGetter getDataGetter(String path) throws MetadataException;

    /**
     * returns the data value in its <code>String</code> form or
     * <code>null</code> if the value is not set. Most of the simple types
     * return the <code>String</code> in the form you would get if you invoked
     * the <code>toString</code> method on an object of that type. The Date type
     * is the principal exception. It formats the date and time using the
     * following format pattern as specified for the standard
     * <code>java.text.SimpleDateFormat</code>:
     * 
     * <pre>
     *      yyyy-MM-ddTHH:mm:ss.SSSZ
     * </pre>
     * 
     * For example, the date/time for March 30, 2005, 2:15 PM PST is shown as:
     * 
     * <pre>
     *      2005-03-30T14:15:00.000Z
     * </pre>
     * @return the data value in its <code>String</code> form or <code>null</code> if the value is not set
     * 
     * @throws MetadataException thrown when properties of this type do not
     *         support a string representation
     */
    String getAsString() throws MetadataException;

    /**
     * Returns the data value at the specified path in its <code>String</code>
     * form.
     * 
     * @param path the path to the data being retrieved relative to this
     *        property
     * @return the property value in its <code>String</code> form or
     *         <code>null</code> if the property is not set
     * @throws MetadataException thrown when the path is not valid for the
     *         underlying {@link DataType}.
     */
    String getAsString(String path) throws MetadataException;

    /**
     * Returns the string representation of the data value formatted using the
     * specified format object. If the format object is <code>null</code> this
     * method behaves as the {@link #getAsString()} method does.
     * 
     * @param format the format object used to format the underlying value
     * @return the string representation of the data value formatted using the
     *         specified format object
     * @throws MetadataException thrown when there is an error returning a
     * formatted string
     */
    String getAsString(Format format) throws MetadataException;

    /**
     * Returns the data value at the specified path in its <code>String</code>
     * form formatted using the specified format object. If the format object is
     * <code>null</code> this method behaves as the {@link #getAsString(String)}
     * method.
     * 
     * @param path the path to the data being retrieved relative to this
     *        property
     * @param format the format object used to format the underlying value
     * @return the property value in its <code>String</code> form or
     *         <code>null</code> if the property is not set
     * @throws MetadataException thrown when the path is not valid for the
     *         underlying {@link DataType}.
     */
    String getAsString(String path, Format format) throws MetadataException;

    /**
     * Return the data value for this data object or <code>null</code> if the
     * data object value is not set.
     *
     * @param <T> the return type
     * @return the data value for this data object or <code>null</code> if the
     *         data object value is not set
     * 
     * @throws MetadataException thrown when there is an error creating a value
     */
    <T> T get() throws MetadataException;

    /**
     * Return the data value from the provided path
     *
     * @param <T> the return type
     * @param path the path identifying the field to return
     * @return the object data value at the specified path
     * 
     * @throws MetadataException thrown when the path does not match the
     *         underlying data type or there is an error creating a value
     */
    <T> T get(String path) throws MetadataException;

    /**
     * Returns the relative path names of the direct children of this map-like
     * data. Note that these may not necessary be the display names for the
     * children elements. The collection includes all the names of child
     * elements declared with the map-like data type, including those that might
     * be known but currently have no value (as in the case of a struct).
     * @return the relative path names of the direct children of this map-like data
     * 
     * @throws MetadataException thrown when the path does not match a struct in
     *         the underlying property type
     */
    Collection<String> getMapElementNames() throws MetadataException;

    /**
     * Returns the relative path names of the direct children of the map at the
     * specified path. Note that these may not necessary be the display names
     * for the children elements. The collection includes all the names of child
     * elements declared with the map-like data type, including those that might
     * be known but currently have no value (as with a struct type).
     * 
     * @param path the path to the struct data relative to this one
     * @return the relative path names of the direct children of the map at the
     * specified path
     * 
     * @throws MetadataException thrown when the path does not match a struct in
     *         the underlying property type
     */
    Collection<String> getMapElementNames(String path) throws MetadataException;

    /**
     * @return the number of elements in this array or list data.
     * 
     * @throws MetadataException thrown when the specified path does not match
     *         an array or list in the underlying data type
     */
    int size() throws MetadataException;

    /**
     * Returns the number of elements in an array or list at the specified path.
     * 
     * @param path path to the array or list data relative to this one
     * @return the number of elements in an array or list at the specified path
     * 
     * @throws MetadataException thrown when the specified path does not match
     *         an array or list in the underlying data type
     */
    int size(String path) throws MetadataException;

    /**
     * Returns an editable copy of this data object updated based on the
     * provided map of values where the key for each entry is the path to a
     * simple data type within this structure and the value is the string
     * representation of the value. Validation errors are localized and recorded
     * to the provided error recorder. The validation errors recorded in the
     * error recorder are associated with the provided path as the "data ID" in
     * the error recorder.
     * 
     * @param values the map of values where the key for each entry is the path
     *        to a simple data type within this structure and the value is the
     *        string representation of the value
     * @param errRecorder the object to capture validation errors
     * @return an editable copy of this data object or <code>null</code> if the
     *         data object did not change
     */
    DataSetter setStringData(
        Map<String, String> values,
        DataErrorRecorder errRecorder);

}
