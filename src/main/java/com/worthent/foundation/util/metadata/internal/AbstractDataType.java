package com.worthent.foundation.util.metadata.internal;

import java.text.Format;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.lang.StringUtils;
import com.worthent.foundation.util.metadata.Converter;
import com.worthent.foundation.util.metadata.ConverterType;
import com.worthent.foundation.util.metadata.DataGetter;
import com.worthent.foundation.util.metadata.DataSetter;
import com.worthent.foundation.util.metadata.DataType;
import com.worthent.foundation.util.metadata.DictionaryReference;
import com.worthent.foundation.util.metadata.MetadataException;
import com.worthent.foundation.util.metadata.TypeCode;
import com.worthent.foundation.util.metadata.TypeDictionary;
import com.worthent.foundation.util.metadata.Validator;
import com.worthent.foundation.util.metadata.internal.DataObject.NamedString;
import com.worthent.foundation.util.recorder.DataErrorRecorder;
import com.worthent.foundation.util.recorder.ErrorReporter;
import com.worthent.foundation.util.recorder.RecorderFactory;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Base class for all data types.
 * 
 * @author Erik K. Worth
 */
public abstract class AbstractDataType implements DataType {

    /**
     * Serial Version ID
     */
    private static final long serialVersionUID = 5533138105222911456L;

    /**
     * Class representing a path. The complex data types that contain elements
     * have inner classes that extend this class to provide a Pattern able to
     * match the first relevant part of the path. For example, the map types
     * extend this class with a Pattern able to extract a map key from the path
     * that identifies an element in the map. The list types extend this class
     * with a Pattern able to extract the list index identifying an element in
     * the list.
     */
    protected abstract static class Path {

        /** The original path */
        private final String path;

        /** The compiled Regular Expression matcher */
        private final Matcher matcher;

        /**
         * Construct with the received path
         *
         * @param pattern the regular expression pattern
         * @param path the path string
         */
        protected Path(final Pattern pattern, final String path) {
            this.path = path;
            matcher = pattern.matcher(path);
        }

        /** @return  the string path */
        @Override
        public final String toString() {
            return path;
        }

        /**
         * Returns a field name from the path or <code>null</code> when the path
         * does not begin with something that matches the field expression.
         * 
         * @return a field name from the path or <code>null</code>
         */
        protected String getNextPathPart() {
            return matcher.matches() ? matcher.group(1) : null;
        }

        /**
         * Returns the remainder of the path following the extracted field or
         * <code>null</code> when no field was found or there is nothing but
         * white space after the field
         * 
         * @return the remainder of the path following the extracted field or
         *         <code>null</code>
         */
        protected String getRemainingPath() {
            return matcher.matches()
                ? StringUtils.trimToNull(path.substring(matcher.end(1)))
                : null;
        }

    } // Path

    /** Start of the error message for unsupported operations */
    private static final String MSG_UNSUPPORTED_OP =
        "Operation not supported for type, '";

    protected static final Set<String> BASE_ATTR_SET =
        Collections.unmodifiableSet(new HashSet<>(Arrays.asList(CLASS, CHOICES, REF_TYPE_ID)));

    //
    // Instance Variables
    //

    /** The type identifier for this type */
    private final TypeCode typeCode;

    /** The type attributes */
    private final Map<String, Object> attrs;

    /** The validators enforcing constraints on the data type */
    private final List<Validator> validators;

    /** The converters set on this data type */
    private final Map<ConverterType, Converter> converters;

    /** Used for internalization only */
    protected AbstractDataType() {
        typeCode = null;
        attrs = null;
        validators = null;
        converters = null;
    }

    /**
     * Construct with a type code.
     * 
     * @param typeCode the type code identifying the basic type of data this class represents
     * @param javaClassName the fully qualified name of the Java class backing this data type
     */
    protected AbstractDataType(
        final TypeCode typeCode,
        final String javaClassName) {
        this.typeCode = checkNotNull(typeCode, "typeCode must not be null");
        attrs = new HashMap<>();
        attrs.put(CLASS, checkNotNull(javaClassName, "javaClassName must not be null"));
        validators = new LinkedList<>();
        converters = new HashMap<>();
    }

    /**
     * Copy constructor
     *
     * @param other the other type to copy
     */
    protected AbstractDataType(final AbstractDataType other) {
        if (null == other) {
            throw new IllegalArgumentException("other must not be null");
        }
        typeCode = other.typeCode;
        attrs = new HashMap<>(other.attrs);
        validators = new LinkedList<>(other.validators);
        converters = new HashMap<>(other.converters);
    }

    /**
     * Construct from data object
     * 
     * @param dataObject the data object holding the state of the data type
     * @throws MetadataException thrown when the data object is not of the
     *             appropriate type
     */
    protected AbstractDataType(final DataGetter dataObject)
        throws MetadataException {
        final MetadataMetadata metaMeta = MetadataMetadata.getInstance();
        this.typeCode = metaMeta.getTypeCode(dataObject);
        attrs = metaMeta.getAttrs(dataObject);
        validators = metaMeta.getValidators(dataObject);
        converters = metaMeta.getConverters(dataObject);
    }

    //
    // MetadataBacked
    //

    /**
     * This base class implementation of the method returns the state metadata
     * for the base class. Derived classes override this method to return an
     * extension of the base type to include state added by the derived classes.
     */
    @Override
    public DataType getStateMetadata() {
        final MetadataMetadata metaMeta = MetadataMetadata.getInstance();
        return metaMeta.getStateMetadata();
    }

    @Override
    public DataGetter getState() throws MetadataException {
        final MetadataMetadata metaMeta = MetadataMetadata.getInstance();
        return metaMeta.populateTypeState(this);
    }

    //
    // Abstract Methods
    //

    /**
     * Returns a deep copy of the specified data.
     * 
     * @param data the data object to copy
     * @return a deep copy of the specified data
     * @throws MetadataException thrown when there is an error making a copy
     */
    protected abstract Object deepCopy(Object data) throws MetadataException;

    /**
     * Return the object representation of the path from its string form.
     * 
     * @param path the string form of the path
     * 
     * @return the object representation of the path from its string form
     */
    protected abstract Path newPath(String path) throws MetadataException;

    //
    // Overrides to Object
    //

    /**
     * Returns <code>true</code> when the base attributes of the this type are
     * all the same as the other.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof RefType) {
            other = ((RefType) other).getReferencedType();
        }
        if (!(other instanceof AbstractDataType)) {
            return false;
        }
        final AbstractDataType that = (AbstractDataType) other;
        return (typeCode.equals(that.typeCode) && attrs.equals(that.attrs));
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeCode, attrs);
    }

    //
    // DataType interface
    //

    /**
     * The base class implementation of this method allows <code>null</code>
     * values and throws a <code>MetadataException</code> when the specified
     * value is not an instance of the underlying class specified for this data
     * type.
     */
    @Override
    public void assertValid(final Object value) throws MetadataException {
        this.safeCast(value);
        final DataErrorRecorder errRecorder =
            RecorderFactory.newDataErrorRecorder();
        for (final Validator validator : validators) {
            validator.validate(value, errRecorder);
            if (errRecorder.hasErrors()) {
                throw new MetadataException(errRecorder.toString());
            } // if there are errors
        } // for each validator
    }

    /**
     * The base class returns the attribute when the name is a member of the
     * {@link #BASE_ATTR_SET} and throws a <code>MetadataException</code>
     * otherwise.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAttribute(final String name) throws MetadataException {
        if (!BASE_ATTR_SET.contains(name)) {
            throw new MetadataException("No such attribute, '" + name + "'");
        }
        return (T) attrs.get(name);
    }

    /**
     * This base class returns the set of attributes known at the base class
     * level. Derived classes that support additional attributes override this
     * method.
     */
    @Override
    public Set<String> getAttributeNames() {
        return BASE_ATTR_SET;
    }

    /**
     * The base implementation of this method always throws
     * <code>MetadataException</code>.
     */
    @Override
    public DataType getChildType(final String name) throws MetadataException {
        throw new MetadataException("The data type, '" + typeCode +
            "', has no child type named, '" + name + "'");
    }

    /**
     * The base implementation always returns zero.
     */
    @Override
    public int getChildTypeCount() {
        return 0;
    }

    /**
     * The base implementation always returns an empty list of child type names.
     */
    @Override
    public Collection<String> getChildrenNames() {
        return Collections.emptyList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.worthent.foundation.service.metadata.DataType#getTypeCode()
     */
    @Override
    public TypeCode getTypeCode() {
        return typeCode;
    }

    /**
     * The base implementation returns <code>true</code> when the specified
     * attribute is present in the attribute map.
     */
    @Override
    public boolean hasAttribute(final String name) {
        return BASE_ATTR_SET.contains(name);
    }

    /**
     * The base implementation always returns <code>false</code>
     */
    @Override
    public boolean hasChild(final String name) {
        return false;
    }

    /**
     * The base implementation always returns <code>false</code>.
     */
    @Override
    public boolean hasInitialValue() {
        return false;
    }

    /**
     * The base implementation always returns <code>false</code>.
     */
    @Override
    public boolean isReference() {
        return false;
    }

    /**
     * The base implementation always returns <code>false</code>.
     */
    @Override
    public boolean isSimpleType() {
        return false;
    }

    /**
     * The base implementation always throws a <code>MetadataException</code>.
     */
    @Override
    public DataSetter newValue() throws MetadataException {
        throw new MetadataException(MSG_UNSUPPORTED_OP + typeCode + "'");
    }

    //
    // Helper Methods
    //

    /**
     * Validates the provided data using all the registered validators for the
     * data type. This method does not validate fields in a nested structure.
     * 
     * @param data the data to be validated
     * @param errRecorder the object to capture any validation errors
     */
    protected void validate(
        final Object data,
        final DataErrorRecorder errRecorder) {
        for (final Validator validator : validators) {
            validator.validate(data, errRecorder);
        }
    }

    /**
     * Validates the provided data recursively depth first. This method is
     * overloaded by complex types that validate their children. The base
     * implementation simply calls validate().
     * 
     * @param data the data to be validated
     * @param errRecorder the object to capture any validation errors
     * @throws MetadataException thrown when the provided data object is not
     *             structurally compatible with the type metadata (e.g. a list
     *             is passed to a type describing a struct)
     */
    protected void deepValidate(
        final Object data,
        final DataErrorRecorder errRecorder) throws MetadataException {
        validate(data, errRecorder);
    }

    /**
     * Sets the value of an attribute. Since type definitions are immutable,
     * this should only be called during the construction of the data type.
     *
     * @param <T> the attribute type
     * @param name the attribute name. Use a well-known name declared in the
     *            {@link DataType} interface or add the well-known name to the
     *            interface if it does not exist
     * @param value the attribute value
     */
    protected <T> void setAttribute(final String name, final T value) {
        attrs.put(name, value);
    }

    /**
     * Adds the validator to the list of validators used to enforce constraints
     * on this data type. Since type definitions are immutable, this should only
     * be called during the construction of the data type.
     * 
     * @param validator the validator to add
     */
    protected void addValidator(final Validator validator) {
        validators.add(validator);
    }

    /**
     * Sets the converter of the specified converter type on the data type. This
     * converter will be used when converting from one type of data to another
     * as governed by the converter type. Since type definitions are immutable,
     * this should only be called during the construction of the data type.
     * 
     * @param converterType the converter type indicating the circumstances when
     *            the specified converter is engaged
     * @param converter the converter used to convert data from one type to
     *            another
     */
    protected void setConverter(
        final ConverterType converterType,
        final Converter converter) {
        converters.put(converterType, converter);
    }

    /**
     * Throws <code>MetadataException</code> if the specified value is non-null
     * and not an instance of the class specified for this data type.
     *
     * @param <T> the target types
     * @param value the object to cast to the target type
     * @return the value cast to the target type
     * @throws MetadataException thrown when the provided value is not an instance of the target type
     */
    @SuppressWarnings("unchecked")
    protected <T> T safeCast(Object value) throws MetadataException {
        if (null == value) {
            return null;
        }
        // Unwrap if wrapped in a data object
        if (value instanceof DataGetter) {
            value = ((DataGetter) value).get();
        }
        final Class<?> javaClass = getJavaClass();
        try {
            assertIsClass(value, javaClass);
        } catch (final ClassCastException exc) {
            throw new MetadataException("Invalid value type.", exc);
        }
        return (T) value;
    }

    /**
     * Returns the Java class name. This is the value of the CLASS attribute.
     * 
     * @return the Java class name
     */
    protected String getClassName() {
        return (String) attrs.get(CLASS);
    }

    /**
     * Returns the Java class from the Java Class Name attribute.
     * 
     * @return the Java class from the Java Class Name attribute
     * @throws MetadataException thrown when there is an error creating the
     *             class from the name
     */
    protected Class<?> getJavaClass() throws MetadataException {
        final String className = (String) attrs.get(CLASS);
        try {
            return Class.forName(className);
        } catch (final Exception exc) {
            throw new MetadataException(
                "Error creating a class from the class name, '" + className +
                    "'",
                exc);
        }
    }

    /**
     * Returns a new instance of the Java object using the default constructor.
     * 
     * @return a new instance of the Java object using the default constructor
     * @throws MetadataException thrown when there is an error creating the new
     *             object instance
     */
    protected Object newInstance() throws MetadataException {
        final Class<?> javaClass = getJavaClass();
        final Object obj;
        try {
            obj = javaClass.newInstance();
        } catch (final Exception exc) {
            throw new MetadataException("Error creating instance of class, '" +
                javaClass.getName() + "'", exc);
        }
        return obj;
    }

    //
    // Methods Supporting Data Objects
    //
    // Data Objects delegate to the type to do most of their work. That makes
    // it easier to interpret the operation on the data object in light of its
    // data type.
    //

    /**
     * The base class always throws a <code>MetadataException</code>. Those
     * derived types that have a string representation override this method.
     *
     * @param data the object to convert to a string
     * @param format the format object used to convert the object string
     * @return the string representation of the object
     */
    protected String asString(final Object data, final Format format)
        throws MetadataException {
        throw new MetadataException(MSG_UNSUPPORTED_OP + typeCode + "'");
    }

    /**
     * The base class always throws a <code>MetadataException</code>. Those
     * derived types that have a string representation override this method.
     *
     * @param value the string representation of the value
     * @return the object data representation of the value
     */
    protected Object fromString(final String value) throws MetadataException {
        throw new MetadataException(MSG_UNSUPPORTED_OP + typeCode + "'");
    }

    /**
     * Converts the object to its string form using a converter if one is
     * registered for the type and the default conversion if one is not.
     * 
     * @param data the data value
     * @param format a formatter used by the default conversion when present
     * @return the string representation of this object value
     * @throws MetadataException thrown when there is an error converting the
     *             data to its string representation
     */
    protected String convertToString(
        final Object data,
        final Format format) throws MetadataException {
        if (null == data) {
            return null;
        }
        final Converter converter = converters.get(ConverterType.TO_STRING);
        if (null == converter) {
            return asString(data, format);
        } else {
            final DataErrorRecorder errRecorder =
                RecorderFactory.newDataErrorRecorder();
            final String strValue =
                (String) converter.convert(data, errRecorder);
            if (errRecorder.hasErrors()) {
                throw new MetadataException(
                    "Error converting value to its string form: " +
                        errRecorder.toString());
            }
            return strValue;
        }
    }

    /**
     * Converts the string value to the object representation using a converter
     * if one is registered with the data type or the default conversion for the
     * data type if not. Any conversion errors are recorded in the specified
     * error recorder
     * 
     * @param value the string value
     * @param errRecorder the object used to record data conversion errors
     * @return the object converted from its string form
     */
    protected Object convertFromString(
        final String value,
        final DataErrorRecorder errRecorder) {
        if (null == value) {
            return null;
        }
        if (null == errRecorder) {
            throw new IllegalArgumentException("errRecorder must not be null");
        }
        final Converter converter = converters.get(ConverterType.FROM_STRING);
        if (null == converter) {
            try {
                return fromString(value);
            } catch (final MetadataException exc) {
                // Error converting the string to an object. Use the default
                // error message
                final String msg = MetadataRsrc.CONVERSION_ERROR.localize(getLocale(), getTypeCode(), value);
                errRecorder.reportError(msg, exc);
                return null;
            }
        } else {
            return converter.convert(value, errRecorder);
        }
    }

    //
    // Operations on the direct elements of Complex Data Objects.
    //
    // These methods are overridden by derived types that support child
    // elements. The base class implementations always throw an exception
    // indicating the operation is not supported. These methods are called
    // by the DataObject implementations.
    //

    /**
     * The base class always throws an exception. Derived types that support
     * adding element values override this method to add element to the data
     * object container.
     *
     * @param data the parent data object
     * @param element the element to add to the parent
     */
    protected void addElement(final Object data, final Object element)
        throws MetadataException {
        throw new MetadataException(MSG_UNSUPPORTED_OP + typeCode + "'");
    }

    /**
     * The base class always throws an exception. Derived types that support
     * adding element values override this method to add a default value to the
     * data object container.
     *
     * @param data the parent data object
     */
    protected void addElement(final Object data) throws MetadataException {
        throw new MetadataException(MSG_UNSUPPORTED_OP + typeCode + "'");
    }

    /**
     * The base class always throws an exception. Derived types that support a
     * key/value structure (e.g. map and struct) override this method to return
     * the list of keys/element names.
     *
     * @param data the map data object
     * @return the map keys
     */
    protected Collection<String> getMapElementNames(final Object data)
        throws MetadataException {
        throw new MetadataException(MSG_UNSUPPORTED_OP + typeCode + "'");
    }

    /**
     * The base class always throws an exception. Derived types that support
     * setting element values override this method to set the element identified
     * by the <code>String</code> item identifier.
     * 
     * @param item identifies the element directly within the data object to be
     *            set
     * @param data the data object containing the element to be set
     * @param element the element to set within the containing data object
     * @throws MetadataException thrown when the operation is not supported or
     *             the data object or item is not valid
     */
    protected void setItem(
        final String item,
        final Object data,
        final Object element) throws MetadataException {
        throw new MetadataException(MSG_UNSUPPORTED_OP + typeCode +
            "', with item, '" + item + "'");
    }

    /**
     * The base class always throws an exception. Derived types that support the
     * removal of elements override this method to remove the element identified
     * by the <code>String</code> item identifier.
     * 
     * @param item identifies the element directly within the data object to be
     *            removed
     * @param data the data object containing the element to be removed
     * @return the removed element
     * @throws MetadataException thrown when the operation is not supported or
     *             the data object or item is not valid
     */
    protected Object removeItem(final String item, final Object data)
        throws MetadataException {
        throw new MetadataException(MSG_UNSUPPORTED_OP + typeCode +
            "', with item, '" + item + "'");
    }

    /**
     * The base class always throws an exception. Derived types that contain
     * data override this method to return the current number of elements
     * currently contained within the specified data object.
     *
     * @param data the parent data object
     * @return the number of child items in the parent
     */
    protected int getSize(final Object data) throws MetadataException {
        throw new MetadataException(MSG_UNSUPPORTED_OP + typeCode + "'");
    }

    //
    // Operations on Data Objects that use a path to identify an element within
    // a hierarchical data structure. The DataObject implementations call
    // these methods.
    //

    /**
     * Adds the specified element to the data object identified by the specified
     * path.
     * 
     * @param path the path identifying the element to add
     * @param data the root data object that will contain the element being
     *            added
     * @param element the element to add
     * @throws MetadataException thrown when there is an error adding the
     *             element
     */
    void addElement(
        final String path,
        final Object data,
        final Object element) throws MetadataException {
        final Path p = newPath(path);
        if (null == p) {
            addElement(data, element);
        } else {
            final String pathPart = getNextPathPart(p);
            final AbstractDataType fieldType =
                getChildTypeFromPathPart(pathPart);
            final Object childValue = getChildValueFromPathPart(pathPart, data);
            final String remainingPath = getRemainingPath(p);
            if (StringUtils.isBlank(remainingPath)) {
                fieldType.addElement(childValue, element);
            } else {
                fieldType.addElement(remainingPath, childValue, element);
            }
        }
    }

    /**
     * Return the string representation of the specified data object.
     * 
     * @param path the path to the desired data relative to the specified data
     * @param data the data object at the starting point for the specified path
     * @param format the format to use or <code>null</code> to use the default
     *            string representation
     * @return the string representation of the specified data object
     * @throws MetadataException thrown when there is an error producing the
     *             data object at the specified path
     */
    String asString(
        final String path,
        final Object data,
        final Format format) throws MetadataException {
        final Path p = newPath(path);
        if (null == p) {
            return convertToString(data, format);
        }
        final String pathPart = getNextPathPart(p);
        final AbstractDataType fieldType = getChildTypeFromPathPart(pathPart);
        final Object childValue = getChildValueFromPathPart(pathPart, data);
        final String remainingPath = getRemainingPath(p);
        if (StringUtils.isBlank(remainingPath)) {
            return fieldType.convertToString(childValue, format);
        } else {
            return fieldType.asString(remainingPath, childValue, format);
        }
    }

    /**
     * Return the data object at the specified path relative to the specified
     * data object.
     * 
     * @param path the path to the desired data relative to the specified data
     * @param data the data object at the starting point for the specified path
     * @return the data object at the specified path relative to the specified
     *         data object
     * @throws MetadataException thrown when there is an error producing the
     *             data object at the specified path
     */
    Object get(final String path, final Object data)
        throws MetadataException {
        final Path p = newPath(path);
        if (null == p) {
            return safeCast(data);
        }
        final String pathPart = getNextPathPart(p);
        final AbstractDataType fieldType = getChildTypeFromPathPart(pathPart);
        final Object childValue = getChildValueFromPathPart(pathPart, data);
        final String remainingPath = getRemainingPath(p);
        if (StringUtils.isBlank(remainingPath)) {
            return childValue;
        } else {
            return fieldType.get(remainingPath, childValue);
        }
    }

    /**
     * Returns a <code>DataObject</code> wrapping the backing data and type at
     * the specified path below the specified data object container.
     * 
     * @param path the path to the desired data relative to the specified data
     * @param data the data object at the starting point for the specified path
     * @return the data object at the specified path relative to the specified
     *         data object
     * @throws MetadataException thrown when there is an error producing the
     *             data object at the specified path
     */
    DataObject getDataObject(final String path, final Object data)
        throws MetadataException {
        final Path p = newPath(path);
        if (null == p) {
            return new DataObject(this, safeCast(data));
        }
        final String pathPart = getNextPathPart(p);
        final AbstractDataType childType = getChildTypeFromPathPart(pathPart);
        final Object childValue = getChildValueFromPathPart(pathPart, data);
        final String remainingPath = getRemainingPath(p);
        if (StringUtils.isBlank(remainingPath)) {
            return new DataObject(childType, childValue);
        } else {
            return childType.getDataObject(remainingPath, childValue);
        }
    }

    /**
     * Returns an <code>EditableDataObject</code> wrapping the backing data and
     * type at the specified path below the specified data object container.
     * 
     * @param path the path to the desired data relative to the specified data
     * @param data the data object at the starting point for the specified path
     * @return the data object at the specified path relative to the specified
     *         data object
     * @throws MetadataException thrown when there is an error producing the
     *             data object at the specified path
     */
    EditableDataObject getEditableDataObject(
        final String path,
        final Object data) throws MetadataException {
        final Path p = newPath(path);
        if (null == p) {
            return new EditableDataObject(this, safeCast(data));
        }
        final String pathPart = getNextPathPart(p);
        final AbstractDataType childType = getChildTypeFromPathPart(pathPart);
        final Object childValue = getChildValueFromPathPart(pathPart, data);
        final String remainingPath = getRemainingPath(p);
        if (StringUtils.isBlank(remainingPath)) {
            return new EditableDataObject(childType, childValue);
        } else {
            return childType.getEditableDataObject(remainingPath, childValue);
        }
    }

    /**
     * Return the number of elements directly contained in the data object at
     * the specified path relative to the specified data object.
     * 
     * @param path the path to the desired data relative to the specified data
     * @param data the data object at the starting point for the specified path
     * @return the number of elements directly contained in the data object at
     *         the specified path relative to the specified data object
     * @throws MetadataException thrown when there is an error producing the
     *             number of elements at the specified path
     */
    int getSize(final String path, final Object data)
        throws MetadataException {
        final Path p = newPath(path);
        if (null == p) {
            return getSize(data);
        }
        final String pathPart = getNextPathPart(p);
        final AbstractDataType fieldType = getChildTypeFromPathPart(pathPart);
        final Object childValue = getChildValueFromPathPart(pathPart, data);
        final String remainingPath = getRemainingPath(p);
        if (StringUtils.isBlank(remainingPath)) {
            return fieldType.getSize(childValue);
        } else {
            return fieldType.getSize(remainingPath, childValue);
        }
    }

    /**
     * Returns the keys for the named items in a map-like data type at the
     * specified path relative to the root data object.
     * 
     * @param path the path to the desired map-like data relative to the
     *            specified data
     * @param data the data object at the starting point for the specified path
     * @return the keys for the named items in a map-like data type at the
     *         specified path relative to the root data object
     * @throws MetadataException thrown when there is an error producing the
     *             named items
     */
    Collection<String> getMapElementNames(
        final String path,
        final Object data) throws MetadataException {
        final Path p = newPath(path);
        if (null == p) {
            return getMapElementNames(data);
        }
        final String pathPart = getNextPathPart(p);
        final AbstractDataType fieldType = getChildTypeFromPathPart(pathPart);
        final Object childValue = getChildValueFromPathPart(pathPart, data);
        final String remainingPath = getRemainingPath(p);
        if (StringUtils.isBlank(remainingPath)) {
            return fieldType.getMapElementNames(childValue);
        } else {
            return fieldType.getMapElementNames(remainingPath, childValue);
        }
    }

    /**
     * Returns the data type of the element at the specified path.
     * 
     * @param path the path into the type definition
     * @return the data type of the element at the specified path
     * @throws MetadataException thrown when there is an error accessing the
     *             data type at the specified path
     */
    AbstractDataType getType(final String path) throws MetadataException {
        final Path p = newPath(path);
        if (null == p) {
            return this;
        }
        final String pathPart = getNextPathPart(p);
        final AbstractDataType fieldType = getChildTypeFromPathPart(pathPart);
        final String remainingPath = getRemainingPath(p);
        if (StringUtils.isBlank(remainingPath)) {
            return fieldType;
        } else {
            return fieldType.getType(remainingPath);
        }
    }

    /**
     * Removes an element from the specified data object identified by the
     * specified path
     * 
     * @param path the path to the element to remove
     * @param data the root data object containing the element to be removed
     * @throws MetadataException thrown when there is an error removing the
     *             specified element
     */
    Object removeElement(final String path, final Object data)
        throws MetadataException {
        final Path p = newPath(path);
        if (null == p) {
            throw new MetadataException(MSG_UNSUPPORTED_OP + typeCode + "'");
        }
        final String pathPart = getNextPathPart(p);
        final AbstractDataType fieldType = getChildTypeFromPathPart(pathPart);
        final Object childValue = getChildValueFromPathPart(pathPart, data);
        final String remainingPath = getRemainingPath(p);
        if (StringUtils.isBlank(remainingPath)) {
            return fieldType.removeItem(pathPart, data);
        } else {
            return fieldType.removeElement(p.getRemainingPath(), childValue);
        }
    }

    /**
     * Sets the specified element into the a complex data object specified at
     * the path relative to the root data object.
     * 
     * @param path the path to the data to set relative to the provided data
     *            object
     * @param data the root data object from which the path identifies the
     *            element to set
     * @param element the element data to set
     * @throws MetadataException thrown when there is an error setting the
     *             element data at the path below the root data object
     */
    void set(final String path, final Object data, final Object element)
        throws MetadataException {
        final Path p = newPath(path);
        if (null == p) {
            throw new MetadataException(MSG_UNSUPPORTED_OP + typeCode + "'");
        }
        final String pathPart = getNextPathPart(p);
        final AbstractDataType fieldType = getChildTypeFromPathPart(pathPart);
        final String remainingPath = getRemainingPath(p);
        try {
            if (StringUtils.isBlank(remainingPath)) {
                fieldType.assertValid(element);
                setItem(pathPart, data, element);
            } else {
                Object childValue = getChildValueFromPathPart(pathPart, data);
                if (fieldType.isReference() && (null == childValue)) {
                    // Create a new item type now
                    final AbstractDataType childFieldType =
                        ((RefType) fieldType).getReferencedTypeChecked();
                    final DataSetter val = childFieldType.newValue();
                    childValue = val.get();
                    this.setItem(pathPart, data, childValue);
                }
                fieldType.set(remainingPath, childValue, element);
            }
        } catch (final MetadataException exc) {
            throw new MetadataException(
                "Unable to set value for item at path, " + p,
                exc);
        }
    }

    /**
     * Sets the specified element into the a complex data object specified at
     * the path relative to the root data object.
     * 
     * @param path the path to the data to set relative to the provided data
     *            object
     * @param data the root data object from which the path identifies the
     *            element to set
     * @param value the element data to set
     * @param errRecorder the object used to report an error when the specified
     *            <code>String</code> value cannot be converted its object form
     * @throws MetadataException thrown when there is an error setting the
     *             element data at the path below the root data object
     */
    void setFromString(
        final String path,
        final Object data,
        final String value,
        final DataErrorRecorder errRecorder) throws MetadataException {
        final Path p = newPath(path);
        if (null == p) {
            throw new MetadataException(MSG_UNSUPPORTED_OP + typeCode + "'");
        }
        final String pathPart = getNextPathPart(p);
        final AbstractDataType fieldType = getChildTypeFromPathPart(pathPart);
        final String remainingPath = getRemainingPath(p);
        if (StringUtils.isBlank(remainingPath)) {
            final int errCnt = errRecorder.getErrorCount();
            final Object element =
                fieldType.convertFromString(value, errRecorder);
            if (errCnt == errRecorder.getErrorCount()) {
                // Set the new value when there is no conversion error
                setItem(pathPart, data, element);
            }
        } else {
            Object childValue = getChildValueFromPathPart(pathPart, data);
            if (fieldType.isReference() && (null == childValue)) {
                // Create a new item type now
                final AbstractDataType childFieldType =
                    ((RefType) fieldType).getReferencedTypeChecked();
                final DataSetter val = childFieldType.newValue();
                childValue = val.get();
                this.setItem(pathPart, data, childValue);
            }
            // Update the data recorder with the path to the field being set
            final String priorDataId = errRecorder.getDataId();
            try {
                final String newDataId = priorDataId + pathPart;
                errRecorder.setDataId(newDataId);

                fieldType.setFromString(
                    remainingPath,
                    childValue,
                    value,
                    errRecorder);
            } finally {
                // Restore the data ID in the error recorder
                errRecorder.setDataId(priorDataId);
            }
        }
    }

    /**
     * Updates the specified data object from the list of new values and returns
     * <code>true</code> if the data object changed. Each new value contains the
     * path to the field to be updated relative to this data object, and the
     * string representation of the new value. The string values are converted
     * to their object form, validated and used to update the corresponding
     * fields of the data object identified by the path name.
     * 
     * @param data the top-level complex data object to be updated
     * @param strData the list of named values containing the path to the fields
     *            relative to the top-level data object and the new values in
     *            their string form
     * @param errRecorder the object used to capture validation errors
     * @return <code>true</code> when the data object is modified
     */
    boolean setStringData(
        final Object data,
        final List<NamedString> strData,
        final DataErrorRecorder errRecorder) {

        // Make sure there is something to do here
        if (strData.isEmpty()) {
            return false;
        }

        // Set this to true when the data object is changed
        boolean changed = false;

        // Iterate over each string data item
        final Iterator<NamedString> it = strData.iterator();

        if (TypeCode.LIST.equals(getTypeCode())) {
            // Extend the list to make sure the indexed value is in range
            try {
                extendList(data, strData);
            } catch (final Exception exc) {
                reportInternalError(errRecorder, exc);
                return false;
            }
        }

        // Process the next named value
        NamedString strValue = it.next();

        do {

            // The name is the path to the field relative to the provided
            // data object
            final String path = strValue.getName();
            final Path p;
            try {
                p = newPath(path);
            } catch (final MetadataException exc) {
                reportInternalError(errRecorder, exc);
                return false;
            }

            // Get the next part of the path identifying the direct child of
            // this data object
            final String pathPart;
            try {
                pathPart = getNextPathPart(p);
            } catch (final MetadataException exc) {
                reportInternalError(errRecorder, exc);
                return false;
            }

            // Get the data type of the direct child of this data object
            // identified by the path part
            final AbstractDataType fieldType;
            try {
                fieldType = getChildTypeFromPathPart(pathPart);
            } catch (final MetadataException exc) {
                reportInternalError(errRecorder, exc);
                return false;
            }

            // Get the rest of the path (skipping over any delimiters)
            final String remainingPath = getRemainingPath(p);
            if (StringUtils.isBlank(remainingPath)) {

                if (TypeCode.LIST.equals(fieldType.getTypeCode())) {
                    // Special case. Ignore the provided value and instead add a
                    // new element to the list.
                    changed = addListElement(data, fieldType, pathPart, errRecorder) || changed;
                } else {
                    // The child is a simple type
                    changed = setSimpleValue(data, fieldType, pathPart, strValue, errRecorder) || changed;
                }
            } else {

                // The child is a complex data type
                changed = setComplexValue(data, fieldType, p, it, pathPart, strValue, errRecorder) || changed;
            }
            // Get the next string value to process if there is one
            strValue = it.hasNext() ? it.next() : null;
        } while (null != strValue);
        return changed;
    }

    /**
     * Set a simple child value on the parent data object
     * @param parentData the parent data object
     * @param fieldType the field type definition
     * @param pathPart the part of the path identifying the field
     * @param strValue the string data to set on the child value
     * @param errRecorder the error recorder
     * @return <code>true</code> when the data structure has been modified
     */
    private boolean setSimpleValue(
            final Object parentData,
            final AbstractDataType fieldType,
            final String pathPart,
            final NamedString strValue,
            final DataErrorRecorder errRecorder) {
        // The child is a simple type
        final String priorDataId = errRecorder.getDataId();
        boolean changed = false;
        try {
            // Update the data recorder with the path to the field
            // being set
            final String priorPath = (null == priorDataId) ? "" : priorDataId;
            final String newDataId = priorPath + pathPart;
            errRecorder.setDataId(newDataId);
            final int errCnt = errRecorder.getErrorCount(newDataId);

            // Convert the string value to its object form.
            final Object element = fieldType.convertFromString(
                    strValue.getValue(),
                    errRecorder);

            // Validate the element data using the validators
            // registered for this field
            if (errCnt == errRecorder.getErrorCount(newDataId)) {
                fieldType.validate(element, errRecorder);
            }

            // Set the new value if the new value is valid and it is
            // different from the prior value
            if (errCnt == errRecorder.getErrorCount(newDataId)) {
                final Object priorValue =
                        getChildValueFromPathPart(pathPart, parentData);
                if (!Objects.equals(priorValue, element)) {
                    setItem(pathPart, parentData, element);
                    changed = true;
                }
            }
        } catch (final MetadataException exc) {
            reportInternalError(errRecorder, exc);
        } finally {
            // Restore the data ID in the error recorder
            errRecorder.setDataId(priorDataId);
        }
        return changed;
    }

    /**
     * Set a complex data object field from string values.
     *
     * @param parentData the parent data object
     * @param fieldType the child field definition
     * @param p the path object to the element
     * @param it the iterator over the string data
     * @param pathPart the current path part identifying the complex child element
     * @param firstStrValue the first string data value
     * @param errRecorder the error recorder
     * @return <code>true</code> when the data structure has been modified
     */
    private boolean setComplexValue(
            final Object parentData,
            final AbstractDataType fieldType,
            final Path p,
            final Iterator<NamedString> it,
            final String pathPart,
            final NamedString firstStrValue,
            final DataErrorRecorder errRecorder) {

        // The child is a complex data type
        final String remainingPath = getRemainingPath(p);
        final String path = firstStrValue.getName();
        boolean changed;
        Object childValue;
        try {
            childValue = getChildValueFromPathPart(pathPart, parentData);
            if (fieldType.isReference() && (null == childValue)) {
                // Create a new item type now
                final AbstractDataType childFieldType = ((RefType) fieldType).getReferencedTypeChecked();
                final DataSetter val = childFieldType.newValue();
                childValue = val.get();
                this.setItem(pathPart, parentData, childValue);
            }
        } catch (final MetadataException exc) {
            reportInternalError(errRecorder, exc);
            return false;
        }

        // Gather all the input string values that have the same prefix
        final List<NamedString> fldData = new LinkedList<>();
        fldData.add(firstStrValue);
        firstStrValue.setName(remainingPath);
        while (it.hasNext()) {
            final NamedString strValue = it.next();
            final Path itemPath;
            try {
                itemPath = newPath(strValue.getName());
            } catch (final MetadataException exc) {
                reportInternalError(errRecorder, exc);
                continue;
            }
            final String itemName = strValue.getName();
            if (itemName.startsWith(pathPart)) {
                final String rest = getRemainingPath(itemPath);
                fldData.add(strValue);
                strValue.setName(rest);
            } else {
                break;
            }
        } // for each string data item with the same prefix

        final String priorDataId = errRecorder.getDataId();
        try {
            // The new data ID is everything up to the remaining path
            final int fldPos = path.indexOf(remainingPath);
            final String priorPath = (priorDataId == null) ? "" : priorDataId;
            final String newDataId = priorPath + path.substring(0, fldPos);
            errRecorder.setDataId(newDataId);

            // Update the data object from the relevant fields
            changed = fieldType.setStringData(childValue, fldData, errRecorder);
        } finally {
            errRecorder.setDataId(priorDataId);
        }
        return changed;
    }

    /**
     * Add a list element to the list item that is a member of the parent data object
     * @param parentData the parent data object
     * @param fieldType the field type definition
     * @param pathPart the part of the path identifying the list member
     * @param errRecorder the error recorder
     * @return <code>true</code> when the data structure is modified
     */
    private boolean addListElement(
            final Object parentData,
            final AbstractDataType fieldType,
            final String pathPart,
            final DataErrorRecorder errRecorder) {
        final String priorDataId = errRecorder.getDataId();
        boolean changed = false;
        try {
            // Update the data recorder with the path to the field being set
            final String newDataId = priorDataId + pathPart;
            errRecorder.setDataId(newDataId);

            // Get the List object from the parent data object
            final Object listObj = getChildValueFromPathPart(pathPart, parentData);

            // Add the default element to the list
            fieldType.addElement(listObj);
            changed = true;
        } catch (final MetadataException exc) {
            reportInternalError(errRecorder, exc);
        } finally {
            errRecorder.setDataId(priorDataId);
        }
        return changed;
    }

    /**
     * This is called when this abstract type represents a list to make sure the list has enough elements based on
     * the provided data path.
     *
     * @param data the data object being updated
     * @param strData the string data used to update the list
     */
    private void extendList(final Object data, final List<NamedString> strData) {
        // The last value has the highest list index
        final NamedString lastValue = strData.get(strData.size() - 1);
        final Path elementPath = newPath(lastValue.getName());
        // Get the list index
        final String strIndex = getNextPathPart(elementPath);
        final int index = Integer.parseInt(strIndex);
        final int size = getSize(data);
        for (int i = size; i <= index; i++) {
            addElement(data);
        }
    }

    //
    // Helper Methods that work with Paths
    //
    // Derived classes may overload these to provide type-specific validation
    //

    /**
     * Returns the next part of the path after validating it
     * 
     * @param path the original path
     * @return the next part of the path
     */
    protected String getNextPathPart(Path path) throws MetadataException {
        final String nextPart = path.getNextPathPart();
        return StringUtils.trimToNull(nextPart);
    }

    /**
     * Returns the remaining part of the path or <code>null</code> if there
     * is nothing left in the path
     *
     * @param path the original path
     * @return the remaining part of the path
     */
    protected String getRemainingPath(Path path) {
        final String remainingPath =
            (null == path) ? null : path.getRemainingPath();
        return StringUtils.trimToNull(remainingPath);
    }

    /**
     * The base class always throws an exception. Derived classes that are able
     * to return a child (element) type given a relevant part of a path override
     * this method to return the child type definition.
     * 
     * @param pathPart the relevant part of the path identifying a direct child
     *            element type
     * @return return a child (element) type given a relevant part of a path
     * @throws MetadataException thrown when the type does not support child or
     *             element types or when the path part does not identify a valid
     *             child type definition
     */
    protected AbstractDataType getChildTypeFromPathPart(final String pathPart)
        throws MetadataException {
        throw new MetadataException(MSG_UNSUPPORTED_OP + typeCode + "'");
    }

    /**
     * The base class always throws an exception. Derived classes that are able
     * to return an element object given the relevant part of a path override
     * this method to return the specified element.
     * 
     * @param pathPart the relevant part of the path identifying an element
     *            within the specified data object
     * @param data the data object that contains the desired element
     * @return the element object given the relevant part of a path
     * @throws MetadataException thrown when the type is not a container with
     *             element objects or when the path part is invalid
     */
    protected Object getChildValueFromPathPart(
        final String pathPart,
        final Object data) throws MetadataException {
        throw new MetadataException(MSG_UNSUPPORTED_OP + typeCode + "'");
    }

    /**
     * Returns <code>true</code> when this type has a reference that refers to a
     * type in the specified dictionary.
     * 
     * @param dictionary the target dictionary
     * @return <code>true</code> when this type has a reference that refers to a
     *         type in the specified dictionary
     */
    protected boolean referencesDictionary(final TypeDictionary dictionary) {
        if (isReference()) {
            final DictionaryReference dictRef =
                ((RefType) this).getDictionaryReference();
            return dictRef.referencesDictionary(dictionary);
        } else if (isSimpleType()) {
            return false;
        } else {
            for (final String childName : getChildrenNames()) {
                try {
                    final AbstractDataType childType =
                        getChildTypeFromPathPart(childName);
                    if (childType.referencesDictionary(dictionary)) {
                        return true;
                    }
                } catch (final MetadataException exc) {
                    throw new IllegalStateException("Error getting child, " +
                        childName, exc);
                }
            }
        }
        return false;
    }

    /**
     * Updates all reference types that reference the specified dictionary with
     * a new dictionary reference.
     * 
     * @param dictionary the previously referenced dictionary
     * @param dictRef the new dictionary reference
     */
    protected void replaceDictionaryReference(
        final TypeDictionary dictionary,
        final DictionaryReference dictRef) {
        if (isReference()) {
            final RefType refType = (RefType) this;
            refType.setDictionaryReference(dictRef);
        } else if (!isSimpleType()) {
            for (final String childName : getChildrenNames()) {
                try {
                    final AbstractDataType childType =
                        getChildTypeFromPathPart(childName);
                    childType.replaceDictionaryReference(dictionary, dictRef);
                } catch (final MetadataException exc) {
                    throw new IllegalStateException("Error getting child, " +
                        childName, exc);
                }
            }
        }
    }

    /**
     * Returns the locale currently in effect
     * 
     * @return the locale currently in effect.
     */
    @NotNull
    private static Locale getLocale() {
        return Locale.getDefault();
    }

    /**
     * Throws a <code>MetadataException</code> when the specified object is not
     * an instance of the specified class.
     * 
     * @param obj the object
     * @param javaClass the class
     * @throws ClassCastException thrown when the specified object is not an
     *             instance of the specified class
     */
    static void assertIsClass(final Object obj, final Class<?> javaClass)
        throws ClassCastException {
        if (!javaClass.isInstance(obj)) {
            throw new ClassCastException("Expected an object of type, '" +
                javaClass.getName() +
                "', but received in stead an object of type, '" +
                obj.getClass().getName() + "'");
        }
    }

    /**
     * Report an internal error for the provided exception to the provided error
     * reporter.
     * 
     * @param errReporter the object used to report the internal error
     * @param exc the exception being reported
     */
    private static void reportInternalError(
        final ErrorReporter errReporter,
        final Exception exc) {
        final Locale locale = getLocale();
        final String msg = MetadataRsrc.INTERNAL_ERROR.localize(locale);
        errReporter.reportError(msg, exc);
    }

}
