package com.worthent.foundation.util.metadata.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import com.worthent.foundation.util.lang.StringUtils;
import com.worthent.foundation.util.metadata.DataGetter;
import com.worthent.foundation.util.metadata.DataSetter;
import com.worthent.foundation.util.metadata.DataType;
import com.worthent.foundation.util.metadata.MetadataException;
import com.worthent.foundation.util.metadata.TypeCode;
import com.worthent.foundation.util.recorder.DataErrorRecorder;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Base class for list-like data types
 * 
 * @author Erik K. Worth
 */
public abstract class AbstractListType extends AbstractDataType {

    /** Serial Version ID */
    private static final long serialVersionUID = 641778093274929522L;

    /** Regular expression pattern used to extract a list index */
    protected static final Pattern INDEX_PATTERN =
        Pattern.compile("\\[([0-9]+)].*");

    /** Class representing a path to a field in a map */
    protected static class IndexPath extends Path {
        protected IndexPath(final String path) {
            super(INDEX_PATTERN, path);
        }
    }

    /** The list element type */
    private final AbstractDataType elementType;
    
    /** Used for internalization purposes only */
    protected AbstractListType() {
        super();
        elementType = null;
    }

    /**
     * Construct with the proper type code, concrete list class name, and the
     * list element type
     * 
     * @param typeCode the type code for the concrete list type
     * @param javaClassName the java class name for the concrete list class
     * @param elementType the list element type
     */
    protected AbstractListType(
        final TypeCode typeCode,
        final String javaClassName,
        final DataType elementType) {
        super(typeCode, javaClassName);
        assertIsClass(checkNotNull(elementType, "elementType must not be null"), AbstractDataType.class);
        this.elementType = (AbstractDataType) elementType;
    }

    /**
     * Copy constructor
     *
     * @param other the other list type to copy
     */
    protected AbstractListType(final AbstractListType other) {
        super(other);
        this.elementType = other.elementType;
    }

    /**
     * Construct from data object
     *
     * @param dataObject the data object used to define the list type definition
     */
    protected AbstractListType(final DataGetter dataObject)
        throws MetadataException {
        super(dataObject);
        final MetadataMetadata metaMeta = MetadataMetadata.getInstance();
        this.elementType = metaMeta.getElementType(dataObject);
    }

    /**
     * Returns <code>true</code> if this list type is the same as the other.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof RefType) {
            other = ((RefType) other).getReferencedType();
        }
        if (!(other instanceof AbstractListType)) {
            return false;
        }
        final AbstractListType that = (AbstractListType) other;
        return elementType.equals(that.elementType) && super.equals(other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementType, super.hashCode());
    }

    //
    // Overrides for AbstractDataType
    //

    /**
     * Add the specified element to the list data object.
     * 
     * @param data the data object that must be a list
     * @param element the element data that must be a valid element of the list
     * @throws MetadataException thrown when the data object is not a valid list
     *         or the element is not a valid element
     */
    @Override
    protected void addElement(final Object data, final Object element)
        throws MetadataException {
        final List<Object> list = safeCast(data);
        if (null == list) {
            throw new MetadataException("The list is null");
        }
        final AbstractDataType type = getChildTypeFromPathPart(null);
        type.assertValid(element);
        list.add(element);
    }

    /**
     * Adds a default element value to the end of the list
     * 
     * @param data the data object that must be a list
     * @throws MetadataException thrown when the data object is not a valid list
     *         or there is an error creator or adding the element to the list
     */
    @Override
    protected void addElement(final Object data) throws MetadataException {
        final List<Object> list = safeCast(data);
        if (null == list) {
            throw new MetadataException("The list is null");
        }
        final AbstractDataType type = getChildTypeFromPathPart(null);
        final DataSetter value = type.newValue();
        list.add(value.get());
    }

    /**
     * The base class always throws an exception. Derived types that support
     * setting element values override this method to set the element identified
     * by the <code>String</code> item identifier.
     * 
     * @param item identifies the element directly within the data object to be
     *        set
     * @param data the data object containing the element to be set
     * @param element the element to set within the containing data object
     * @throws MetadataException thrown when the operation is not supported or
     *         the data object or item is not valid
     */
    @Override
    protected void setItem(
        final String item,
        final Object data,
        final Object element) throws MetadataException {
        final List<Object> list = safeCast(data);
        final int index = getIndex(item);
        if (index < 0) {
            throw new MetadataException(
                "Cannot set list element with negative index, " + index);
        }
        final AbstractDataType elementType = getChildTypeFromPathPart(null);
        elementType.assertValid(element);

        // Extend the list if necessary.
        while (list.size() < index) {
            // Add default values to extend the list
            final DataSetter value = elementType.newValue();
            list.add(value.get());
        }

        // Add the element if the specified index is after the last, otherwise
        // replace the element at the specified index
        if (list.size() == index) {
            list.add(element);
        } else {
            list.set(index, element);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.worthent.foundation.service.metadata.DataType#assertValid()
     */
    @Override
    public void assertValid() throws MetadataException {
        // This list type is fine if we can create an instance from its class
        // and cast it to a list.
        final Object obj = newInstance();
        if (!(obj instanceof List<?>)) {
            throw new MetadataException(
                "The java class specified for this list type, '" +
                    getClassName() +
                    "', does not derive from java.util.List.");
        }
    }

    /**
     * Throws if the provided value does not extend from java.util.List or one
     * or more elements are invalid according to the element type.
     */
    @Override
    public void assertValid(final Object value) throws MetadataException {
        final List<Object> list = this.safeCast(value);
        for (final Object element : list) {
            elementType.assertValid(element);
        }
    }

    /**
     * Validates the provided data recursively depth first.
     * 
     * @param data the data to be validated
     * @param errRecorder the object to capture any validation errors
     * @throws MetadataException thrown when the provided data object is not
     *         structurally compatible with the type metadata (e.g. a list is
     *         passed to a type describing a struct)
     */
    @Override
    protected void deepValidate(
        final Object data,
        final DataErrorRecorder errRecorder) throws MetadataException {
        final List<Object> list = this.safeCast(data);

        // Get the element type
        final AbstractDataType fieldType = getChildTypeFromPathPart(null);

        // Validate all the elements in the list
        if (null != list) {
            final String priorDataId = errRecorder.getDataId();
            try {
                String priorPath = (null == priorDataId) ? "" : priorDataId;
                int i = 0;
                for (final Object element : list) {
                    final String newDataId = priorPath + '[' + i + ']';
                    errRecorder.setDataId(newDataId);
                    fieldType.deepValidate(element, errRecorder);
                    i++;
                }
            } finally {
                errRecorder.setDataId(priorDataId);
            }
        }

        // Run the validators registered directly on the map
        validate(data, errRecorder);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object deepCopy(final Object data) throws MetadataException {
        final List<Object> copy = (List<Object>) newInstance();
        final List<Object> orig = safeCast(data);
        final AbstractDataType listElementType = getChildTypeFromPathPart(null);
        for (final Object element : orig) {
            copy.add(listElementType.deepCopy(element));
        }
        return copy;
    }

    /**
     * This always returns the list element type, but it makes sure the caller
     * requested the type using its well-known name.
     */
    @Override
    public DataType getChildType(final String name) throws MetadataException {
        if (!INDEXED_CHILD_NAME.equals(name)) {
            throw new MetadataException("The child type, '" +
                name +
                "', requested for type '" +
                getTypeCode() +
                "', is not valid.  Request the child type with the name, '" +
                INDEXED_CHILD_NAME +
                "', instead.");
        }
        return elementType;
    }

    /**
     * Return the list element type. It ignores the path part.
     * 
     * @param pathPart the relevant part of the path identifying a direct child
     *        element type
     * @return return a child (element) type given a relevant part of a path
     * @throws MetadataException thrown when the type does not support child or
     *         element types or when the path part does not identify a valid
     *         child type definition
     */
    @Override
    protected AbstractDataType getChildTypeFromPathPart(final String pathPart)
        throws MetadataException {
        return elementType;
    }

    /**
     * Returns a list element object given the relevant part of a path. The path
     * must be a valid integer that originally is enclosed in square brackets
     * like this [2]. By the time it gets to this method, the integer value has
     * been extracted from the square brackets but should be a positive integer.
     * If the index is past the last element in the list, this method returns
     * the default value for the element type without extending the list.
     * 
     * @param pathPart the relevant part of the path identifying an element
     *        within the specified data object
     * @param data the data object that contains the desired element
     * @return the element object given the relevant part of a path
     * @throws MetadataException thrown when the type is not a container with
     *         element objects or when the path part is invalid
     */
    @Override
    protected Object getChildValueFromPathPart(
        final String pathPart,
        final Object data) throws MetadataException {
        final List<Object> list = safeCast(data);
        final int index = getIndex(pathPart);
        if (index >= list.size()) {
            // Return the default element type
            final AbstractDataType elementType = getChildTypeFromPathPart(null);
            return elementType.newInstance();
        }
        return list.get(index);
    }

    /**
     * Always returns one because the child type is the element type.
     */
    @Override
    public int getChildTypeCount() {
        return 1;
    }

    /**
     * Returns the name of the list element type.
     */
    @Override
    public Collection<String> getChildrenNames() {
        return Collections.singletonList(INDEXED_CHILD_NAME);
    }

    /**
     * Returns the integer list index from a path starting with a list index.
     * 
     * @param path the original path
     * @return the integer list index from a path starting with a list index
     * @throws MetadataException thrown when there is no valid list index at the
     *         start of the path
     */
    @Override
    protected String getNextPathPart(final Path path) throws MetadataException {
        final String indexStr = path.getNextPathPart();
        assertValidIndex(indexStr);
        return indexStr;
    }

    /**
     * Returns the remaining part of the path or <code>null</code> if there
     * is nothing left in the path
     */
    @Override
    protected String getRemainingPath(Path path) {
        if (null == path) {
            return null;
        }
        String remainingPath = StringUtils.trimToNull(path.getRemainingPath());
        if ((null != remainingPath) && remainingPath.startsWith("]")) {
            // Skip the index delimiter
            remainingPath = remainingPath.substring(1);
        }
        return remainingPath;
    }

    /**
     * Returns the number of elements in the list.
     */
    @Override
    protected int getSize(final Object data) throws MetadataException {
        final List<Object> list = safeCast(data);
        return list.size();
    }

    /**
     * Returns <code>true</code> if the specified name is the name of a field.
     */
    @Override
    public boolean hasChild(final String name) {
        return INDEXED_CHILD_NAME.equals(name);
    }

    /**
     * List-based data structures can always return an initial value.
     */
    @Override
    public boolean hasInitialValue() {
        return true;
    }

    /**
     * Returns the path object for the string path.
     */
    protected Path newPath(final String path) {
        final String trimmed = StringUtils.trimToNull(path);
        return (null == trimmed) ? null : new IndexPath(trimmed);
    }

    /**
     * Creates an empty map and wraps it in data setter.
     */
    @Override
    public DataSetter newValue() throws MetadataException {
        final Object list = newInstance();
        return new EditableDataObject(this, list);
    }

    //
    // Helper Methods
    //

    /**
     * Throws an exception if the provided index string is not valid
     * 
     * @param indexStr the string form of an integer index
     * @throws MetadataException thrown when the specified index string is not a
     *         valid integer
     */
    protected void assertValidIndex(final String indexStr)
        throws MetadataException {
        if (null == indexStr) {
            throw new MetadataException("Null index string");
        }
        try {
            Integer.valueOf(indexStr);
        } catch (final Exception exc) {
            throw new MetadataException("Error converting index string, '" +
                indexStr +
                "', to an integer", exc);
        }
    }

    /**
     * Returns the integer list index given the index string without the square
     * brackets
     * 
     * @param indexStr the string form of the list index
     * @return the integer list index
     * @throws MetadataException thrown when the index string is not a valid
     *         integer
     */
    protected final int getIndex(final String indexStr)
        throws MetadataException {
        final int index;
        try {
            index = Integer.parseInt(indexStr);
        } catch (final Exception exc) {
            throw new MetadataException("The provided path element, '" +
                indexStr +
                "', is not a valid integer index", exc);
        }
        return index;
    }

}
