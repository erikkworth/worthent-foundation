package com.worthent.foundation.util.metadata.internal;

import java.util.Map;
import java.util.regex.Pattern;

import com.worthent.foundation.util.lang.StringUtils;
import com.worthent.foundation.util.metadata.DataGetter;
import com.worthent.foundation.util.metadata.DataType;
import com.worthent.foundation.util.metadata.MetadataException;
import com.worthent.foundation.util.metadata.TypeCode;

/**
 * Base class for data types that have named children.
 * 
 * @author Erik K. Worth
 */
public abstract class AbstractMapType extends AbstractDataType {

    /** Serial ID*/
    private static final long serialVersionUID = 3913068073296330715L;
    
    /** Regular expression pattern used to extract a field name */
    protected static final Pattern FIELD_PATTERN =
        Pattern.compile("([^.\\[]*).*");

    /** Class representing a path to a field in a map */
    protected static class FieldPath extends Path {
        protected FieldPath(final String path) {
            super(FIELD_PATTERN, path);
        }
    }
    
    /** Used for internalization purposes only */
    protected AbstractMapType() {
        super();
    }

    /**
     * Construct with the name of the concrete java class implementing the
     * backing data structure.
     *
     * @param typeCode the type code for this type
     * @param javaClassName the name of the concrete java class implementing the
     *        backing data structure
     */
    protected AbstractMapType(
        final TypeCode typeCode,
        final String javaClassName) {
        super(typeCode, javaClassName);
    }

    /**
     * Copy constructor
     *
     * @param other the type to copy
     */
    protected AbstractMapType(final AbstractMapType other) {
        super(other);
    }

    /**
     * Construct from data object
     *
     * @param dataObject the data object holding the information used to define the map type
     */
    protected AbstractMapType(final DataGetter dataObject)
        throws MetadataException {
        super(dataObject);
    }
    
    //
    // Abstract Methods
    //

    /**
     * Throws an exception if the name of the element is not valid for the type
     * definition. In some cases, the name must match a pre-defined field
     * definition. in other cases, the the name might simply need to have valid
     * characters in it.
     *
     * @param fieldName the name of the field to validate
     */
    protected abstract void assertValidElementName(String fieldName)
        throws MetadataException;

    //
    // Overrides for AbstractDataType
    //

    /**
     * Returns the named field or throws <code>MetadataException</code> if the
     * field is not found.
     */
    @Override
    public DataType getChildType(final String name) throws MetadataException {
        final AbstractDataType childType = getType(name);
        if (null == childType) {
            // Let the base class report the error
            super.getChildType(name);
        }
        return childType;
    }

    /**
     * Map-based data structures can always return an initial value.
     */
    @Override
    public boolean hasInitialValue() {
        return true;
    }

    /**
     * Returns an element object given the relevant part of a path. This method
     * assumes the path part has been validated.
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

        final Map<String, Object> map = safeCast(data);
        return map.get(pathPart);
    }

    /**
     * Returns the name of the field from the path.
     * 
     * @param path the original path encapsulated as an object
     * @return the name of the field from the path
     * @throws MetadataException thrown when the field is not valid for the type
     */
    @Override
    protected String getNextPathPart(final Path path) throws MetadataException {
        final String fieldName = (null == path) ? null : path.getNextPathPart();
        try {
            assertValidElementName(fieldName);
        } catch (final Exception exc) {
            throw new MetadataException("The field, '" +
                fieldName +
                "', extracted from the path, '" +
                path +
                "', is not valid for type, " +
                getTypeCode(), exc);
        }
        return fieldName;
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
        if ((null != remainingPath) && remainingPath.startsWith(".")) {
            // Skip the field delimiter
            remainingPath = remainingPath.substring(1);
        }
        return remainingPath;
    }

    /**
     * Returns the number of elements in the map.
     */
    @Override
    protected int getSize(final Object data) throws MetadataException {
        final Map<String, Object> map = safeCast(data);
        return map.size();
    }

    /**
     * Returns the path object for the string path.
     */
    @Override
    protected Path newPath(final String path) {
        final String trimmed = StringUtils.trimToNull(path);
        return (null == trimmed) ? null : new FieldPath(path);
    }

    /**
     * Set the element identified by the <code>String</code> item identifier.
     * 
     * @param item identifies the element directly within the data object to be
     *        set
     * @param data the data object containing the element to be set
     * @param element the element to set within the containing data object
     * @throws MetadataException thrown when the operation is not supported or
     *         the data object or item is not valid
     */
    protected void setItem(
        final String item,
        final Object data,
        final Object element) throws MetadataException {

        final Map<String, Object> map = safeCast(data);
        map.put(item, element);
    }
}
