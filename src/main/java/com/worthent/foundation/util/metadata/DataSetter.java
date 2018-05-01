package com.worthent.foundation.util.metadata;

import com.worthent.foundation.util.recorder.DataErrorRecorder;

/**
 * Specifies accessors and mutators for the children data within complex data
 * types. The implementation operates on a parent data type that supports nested
 * properties, such as the struct, list and array property types.
 * <p>
 * The path arguments provided may refer to nested structures. If the property
 * type has named fields such as a struct, use a dot ('.') followed by the field
 * name to specify the field. If the property type has indexed elements use the
 * square bracket notation (like arrays) and a numeric zero based index to
 * specify the desired element. Assume we have a data type that describes a
 * struct with a field named "People" that is a list of structs where the inner
 * struct has two fields, one named "LastName" of type String, and the other
 * named "Age" of type Integer. It would look like this:
 * 
 * <pre>
 *     People : List of Struct
 *         LastName : String
 *         Age : Integer
 * </pre>
 * 
 * Let's further assume that property of this type has elements in the the
 * "People" list. The path to reference the age of the second element (with
 * zero-based indexing) would be:
 * 
 * <pre>
 * People[1].Age
 * </pre>
 * 
 * The root property has a <code>null</code> or zero-length path.
 * <p>
 * When an instance of the <code>DataSetter</code> is obtained for a
 * {@link TypeDictionary}, the root type is always a map and the children names
 * are the top-level property types in the dictionary.
 * 
 * @author Erik K. Worth
 */
public interface DataSetter extends DataGetter {

    /**
     * Returns another data setter for the property at the specified path. The
     * backing data for the new data setter is the data at the specified path.
     * The returned data setter operates on the same copy of the underlying data
     * structure as the original.
     * 
     * @param path the path to the desired data
     * @return another data setter for the property at the specified path
     * @throws MetadataException thrown when the path does not properly identify
     *         data in the structure
     */
    DataSetter getDataSetter(String path) throws MetadataException;

    /**
     * Set this data from its string representation. Refer to
     * {@link DataGetter#getAsString} for a discussion of the
     * <code>String</code> representation of property types.
     * 
     * @param value the string representation of the value being set
     * 
     * @throws MetadataException thrown when the string value is invalid for the
     *         specified field type or this type of property does not support
     *         being set from a string
     */
    void setFromString(String value) throws MetadataException;

    /**
     * Set the field at the specified path from its string representation.
     * 
     * @param path the path to the field being set
     * @param value the string representation of the value being set
     * 
     * @throws MetadataException thrown when the string value is invalid for the
     *         specified field type, the path does not match the underlying
     *         property type, or the referenced property does not support being
     *         set from a string
     */
    void setFromString(String path, String value) throws MetadataException;

    /**
     * Set the field at the specified path from its string representation.
     * 
     * @param path the path to the field being set
     * @param value the string representation of the value being set
     * @param errRecorder the object used to record any errors when converting
     *        the value to its object representation
     * @throws MetadataException thrown when there is an error setting the value
     *         at the specified path
     */
    void setFromString(String path, String value, DataErrorRecorder errRecorder)
        throws MetadataException;

    /**
     * Set the value of the backing data object. Constraint checking is
     * performed on the value.
     * 
     * @param value the value to set. A <code>null</code> value is valid unless
     *        the property has a constraint that prevents a <code>null</code>
     *        value.
     * 
     * @throws MetadataException thrown when the object is not valid according
     *         to the type definition or the path does not match the underlying
     *         data type
     */
    void set(Object value) throws MetadataException;

    /**
     * Set the object at the specified path. Constraint checking is performed on
     * the value.
     * 
     * @param path path to the object
     * @param value the value to set. A <code>null</code> value is valid unless
     *        the data type has a constraint that prevents a <code>null</code>
     *        value.
     * 
     * @throws MetadataException thrown when the object is not valid according
     *         to the type definition or the path does not match the underlying
     *         data type
     */
    void set(String path, Object value) throws MetadataException;

    /**
     * Adds an element to a list at the specified path.
     * 
     * @param path path to the array or list data
     * @param element list element to be added
     * 
     * @throws MetadataException thrown when the specified path does not match a
     *         list in the underlying data type
     */
    void addElement(String path, Object element) throws MetadataException;

    /**
     * Removes the element at the specified path from the list. Shifts any
     * subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the list. The path identifies a
     * list element, not the list. For example, if you want to remove the first
     * element of a list field named "Gateways", you would specify this path:
     * Gateways[0]
     * 
     * @param path the path of the element to remove
     * @return the removed element
     * 
     * @throws MetadataException thrown when the specified path does not match
     *         an element in a list
     */
    Object removeElement(String path) throws MetadataException;

    /**
     * Validates the data maintained within the scope of this data setter
     * against the constraints on every corresponding property type and throws
     * an exception if the data at any level in the structure is invalid. It
     * recurses down to each leaf value in the data structure and adds any
     * constraint violation messages to the exception each identifying the path
     * producing the constraint violation. It also reports constraint violations
     * on the composite types as it recurses back up.
     * 
     * @throws MetadataException thrown when a property is not consistent with
     *         its constraints
     */
    void assertValidData() throws MetadataException;

    /**
     * Validates the data recursively depth first using the validators
     * registered on the associated data type definitions. This method assumes
     * the data matches the basic structure of the associated data type and will
     * throw an exception if that is not the case. The main purpose of this
     * method is to generate error messages in the error recorder that could be
     * shown to end users. The messages are localized according to the locale
     * available in the execution context.
     * 
     * @param errRecorder the object to capture any validation errors
     * @throws MetadataException thrown when the provided data object is not
     *         structurally compatible with the type metadata (e.g. a list is
     *         passed to a type describing a struct)
     */
    void validate(DataErrorRecorder errRecorder) throws MetadataException;

}
