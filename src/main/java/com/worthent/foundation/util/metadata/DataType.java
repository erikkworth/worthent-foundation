package com.worthent.foundation.util.metadata;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * Metadata describing simple and complex data structures. This interface
 * describes data types much like a java <code>Class</code> describes an
 * <code>Object</code>. Each instance of <code>DataType</code> describes a
 * single immutable data type. Data types may be simple or complex. The
 * {@link DataSetter} interface provides a wrapper around an instance or value
 * of a <code>DataType</code>. Use the <code>DataSetter</code> to manipulate
 * data values.
 * <p>
 * Simple data types consist of an enumerated type (basically a
 * <code>String</code> that can hold a specific set of values), all the numeric
 * data types that derive from <code>java.lang.Number</code>, and the following
 * additional types represented in java: <code>Boolean</code>, <code>Date</code>,
 * and <code>String</code>.
 * <p>
 * The complex data types include structs, maps and lists. Struct types are like
 * a Java classes with member variables but no methods (they only specify data).
 * Structs have fields where each field has a name, a data type and optionally a
 * default value.
 * <p>
 * List types are much like the java <code>java.util.List</code>. When you
 * declare a list <code>DataType</code>, you need to specify a class that
 * implements the <code>java.util.list</code> when you declare a
 * <code>DataType</code> representing a list. In addition you must specify the
 * specific type of data the list will contain (i.e. its element
 * <code>DataType</code>.
 * <p>
 * The {@link DataTypeFactory} provides a number of factory methods that allow
 * you to declare types. Note that you do not 'declare' most of the simple
 * types, but rather 'get' them from a (default) dictionary because they are
 * pre-declared for you. The only types you need to declare are those that need
 * to be configured. For example, you declare the enumerated type with the
 * specific set of <code>String</code> literals an instance of the type may
 * hold. All of the complex types require configuration and so the factory
 * provides declare methods for each.
 * <p>
 * In addition to simple and complex types, you may also create a
 * <code>DataType</code> that references another <code>DataType</code> in a
 * <code>TypeDictionary</code>. You will need to use a reference type to declare
 * a recursive data structure. For example, if you needed to create a map type
 * to represent a binary tree node, you might declare a reference for the tree
 * node and then the tree node map with child members, LeftBranch and
 * RightBranch where each of these fields use the declared reference to the tree
 * node. You can also use the reference <code>DataType</code> to specify a type
 * that might be declared later based on user input or data from the database.
 * <p>
 * All <code>DataType</code>s have attributes. Attributes provide type-specific
 * information about the type. Every <code>DataType</code> has a CLASS
 * attribute, however only the enumerated <code>DataType</code> has a CHOICES
 * attribute. Use the {@link #getAttributeNames} to get the names of attributes
 * available for a given <code>DataType</code>. Refer to the attribute name
 * constants declare in this interface to know what java type is used to
 * represent the attribute (e.g. CHOICES is a string array --
 * <code>String[]</code>).
 * <p>
 * <code>DataType</code>s may also support constraints that may be used to
 * restrict the values that can be held by a property value of the
 * <code>DataType</code>. For example, numeric types may specify minimum or
 * maximum value constraints. You can use these to declare a positive integer
 * type by setting a minimum value constraint with a zero minimum value.
 * <p>
 * This interface exposes methods that allow one to introspect the child or
 * nested types within a given <code>DataType</code>. Since only complex types
 * have children types, the child-oriented methods return results that indicate
 * there are no children for simple types. For example, if you invoke the
 * {@link #getChildrenNames} method on a <code>DataType</code> representing an
 * <code>Integer</code> it will return <code>null</code>.
 * 
 * @author Erik K. Worth
 */
public interface DataType extends MetadataBacked, Serializable {
    /**
     * The name initial value for this type. Specify this constant in the
     * {@link #getAttribute} method.
     */
    String INITIAL_VALUE = "InitialValue";

    /**
     * The name of the string choices attribute. The attribute value is returned
     * as a List of String. Specify this constant in the {@link #getAttribute}
     * method.
     */
    String CHOICES = "Choices";

    /**
     * The name of the class attribute. Returns a String containing the fully
     * qualified Java class name. Specify this constant in the
     * {@link #getAttribute} method.
     */
    String CLASS = "Class";
    
    /**
     * The name of the type ID attribute for reference types.  The value of
     * this attribute identifies the referenced type in a type dictionary.
     */
    String REF_TYPE_ID = "RefTypeId";

    /** The name of the class from which this type derives */
    String ABSTRACT_CLASS = "AbstractClass";

    /** The well-known name of the list and array element (child) type */
    String INDEXED_CHILD_NAME =
        MetadataRsrc.NAME_LIST_ELEMENT_TYPE_KEY.toString();

    /** The well-known name of the map element (child) type */
    String MAP_CHILD_NAME =
        MetadataRsrc.NAME_MAP_ELEMENT_TYPE_KEY.toString();

    /** @return a copy of the type */
    DataType deepCopy();

    /** @return the type code identifying the basic property type */
    TypeCode getTypeCode();

    /**
     * @return <code>true</code> if this type is a simple type. Simple types can
     * have no nested children types.
     */
    boolean isSimpleType();

    /**
     * @return <code>true</code> if this type is a reference to another type.
     */
    boolean isReference();

    /**
     * Return <code>true</code> if this type definition has an attribute with
     * the specified name.
     * <p>
     * Most type definitions have attributes associated with the data type.
     * Common attributes include initial value, maximum value, minimum value.
     * 
     * @param name the type attribute name
     * @return <code>true</code> if this type definition has the specified
     *         attribute
     */
    boolean hasAttribute(String name);

    /**
     * Returns the type attribute corresponding to the specified name.
     * <p>
     * Most type definitions have attributes associated with the data type.
     * Common attributes include initial value, maximum value, minimum value.
     * 
     * @param <T> the expected type of the attribute
     * @param name the type attribute name
     * @return the type attribute corresponding to the specified name
     * @throws MetadataException thrown when the name does not match a known
     *         attribute for this type
     * @throws ClassCastException thrown when the requested type is not
     *         assignable to the specified template type
     */
    <T> T getAttribute(String name) throws MetadataException;

    /**
     * Returns <code>true</code> if there is an initial value. When this returns
     * <code>true</code> it is safe to invoke the {@link #newValue()} method.
     * 
     * @return <code>true</code> if this type definition has the initial value
     */
    boolean hasInitialValue();

    /**
     * Return the names of the attributes associated with this type definition.
     * 
     * @return the names of the attributes associated with this type definition
     */
    Set<String> getAttributeNames();

    /**
     * Return a <code>DataSetter</code> for a new instance of the property type
     * created according to this type definition.
     * <p>
     * This is a factory method for creating values (variables) of this type.
     * 
     * @return a <code>DataSetter</code> for an instance of the property type
     *         created according to this type definition
     * 
     * @throws MetadataException if there is an error creating the property
     *         setter
     */
    DataSetter newValue() throws MetadataException;

    /**
     * Throws <code>MetadataException</code> if the type has not been properly
     * defined.
     * 
     * @throws MetadataException when the type has not been property defined
     */
    void assertValid() throws MetadataException;

    /**
     * Throws <code>MetadataException</code> if the specified object is not
     * within the constraints of type definition.
     * 
     * @param value the value being checked for validity
     * @throws MetadataException when the specified object is not within the
     *         constraints of type definition
     */
    void assertValid(Object value) throws MetadataException;

    /**
     * Returns the names of the children type elements nested within this type
     * definition.
     * <p>
     * The names are returned in the order in which they are specified when the
     * type was defined (if such a thing makes sense for this type definition).
     * 
     * @return the names of the children type elements nested within this type
     *         definition
     */
    Collection<String> getChildrenNames();

    /**
     * Returns the number of immediately nested child definitions within this
     * definition.
     * 
     * @return the number of nested child definitions within this definition
     */
    int getChildTypeCount();

    /**
     * Returns the type definition for the specified child type.
     * 
     * Composite concrete classes overload this method if they contain children
     * that may be referenced by name.
     * 
     * @param name the name of the child type within this type
     * @return the type definition for the specified child
     * 
     * @throws MetadataException thrown when the name does not match a known
     *         child (nested) type for this type
     */
    DataType getChildType(String name) throws MetadataException;

    /**
     * Returns <code>true</code> if the specified name identifies a nested child
     * type
     * 
     * @param name the name of the nested child type being checked for
     *        membership
     * @return <code>true</code> if the specified name identifies a nested child
     *         type
     */
    boolean hasChild(String name);
    
}
