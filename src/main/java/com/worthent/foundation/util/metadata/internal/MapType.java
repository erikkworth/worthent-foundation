package com.worthent.foundation.util.metadata.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import com.worthent.foundation.util.lang.StringUtils;
import com.worthent.foundation.util.metadata.DataGetter;
import com.worthent.foundation.util.metadata.DataSetter;
import com.worthent.foundation.util.metadata.DataType;
import com.worthent.foundation.util.metadata.MetadataException;
import com.worthent.foundation.util.metadata.TypeCode;
import com.worthent.foundation.util.recorder.DataErrorRecorder;

/**
 * Metadata describing a map data structure. The map has a uniform element type
 * and does not have any predefined keys.
 * 
 * @author Erik K. Worth
 */
public class MapType extends AbstractMapType {

    /** Serial Version ID */
    private static final long serialVersionUID = 2824792844127298200L;
    /** The map element type */
    private final AbstractDataType elementType;

    /** Used for internalization purposes only */
    @SuppressWarnings("unused")
    private MapType() {
        super();
        elementType = null;
    }

    /**
     * Construct from the concrete map class that will back the map data
     * 
     * @param mapClassName the string name of the map class
     * @param elementType the type definition for the map element
     */
    MapType(final String mapClassName, final DataType elementType) {
        super(TypeCode.MAP, mapClassName);
        assertIsClass(elementType, AbstractDataType.class);
        this.elementType = (AbstractDataType) elementType;
    }

    /**
     * Copy constructor
     *
     * @param other the other type to copy
     */
    private MapType(final MapType other) {
        super(other);
        this.elementType = other.elementType;
    }

    /**
     * Construct from data object
     *
     * @param dataObject the data object holding the information used to define this type
     */
    protected MapType(final DataGetter dataObject) throws MetadataException {
        super(dataObject);
        final MetadataMetadata metaMeta = MetadataMetadata.getInstance();
        this.elementType = metaMeta.getElementType(dataObject);
    }

    /**
     * Returns <code>true</code> when the other object represents the same
     * simple type as this one.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof RefType) {
            other = ((RefType) other).getReferencedType();
        }
        if (!(other instanceof MapType)) {
            return false;
        }
        final MapType that = (MapType) other;
        return (elementType.equals(that.elementType) && super.equals(that));
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementType, super.hashCode());
    }

    //
    // Overrides to AbstractDataType
    //

    /**
     * Throws <code>MetadataException</code> if the type has not been properly
     * defined.
     * 
     * @throws MetadataException when the type has not been property defined
     */
    public void assertValid() throws MetadataException {

        if (TypeCode.MAP != getTypeCode()) {
            throw new MetadataException("Expected type code, " +
                TypeCode.MAP +
                ", but found instead, " +
                getTypeCode());
        }

        // This map type is fine if we can create an instance from its class
        // and cast it to a map.
        final Object obj = newInstance();
        if (!(obj instanceof Map<?, ?>)) {
            throw new MetadataException(
                "The java class specified for this map type, '" +
                    getClassName() +
                    "', does not derive from java.util.Map.");
        }
    }

    /**
     * Validates that the specified value is a <code>LinkedHashMap</code> with
     * fields that match the field definition.
     */
    public void assertValid(final Object value) throws MetadataException {
        super.assertValid(value);
        final Map<String, Object> map = safeCast(value);

        // Make sure all the keys and element values in the map are valid
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            assertValidElementName(entry.getKey());
            try {
                elementType.assertValid(entry.getValue());
            } catch (final Exception exc) {
                throw new MetadataException("Invalid value for map field, '" +
                    entry.getKey() +
                    "'", exc);
            }
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
        final Map<String, Object> map = safeCast(data);

        // Get the element type
        final AbstractDataType fieldType = getChildTypeFromPathPart(null);

        // Validate all the elements in the map
        if (null != map) {
            final String priorDataId = errRecorder.getDataId();
            try {
                String priorPath = (null == priorDataId) ? "" : priorDataId;
                if (StringUtils.isNotBlank(priorPath) &&
                    !priorPath.endsWith("]")) {
                    priorPath = priorPath + ".";
                }
                for (final Map.Entry<String, Object> entry : map.entrySet()) {
                    final String newDataId = priorPath + entry.getKey();
                    errRecorder.setDataId(newDataId);
                    fieldType.deepValidate(entry.getValue(), errRecorder);
                }
            } finally {
                errRecorder.setDataId(priorDataId);
            }
        }

        // Run the validators registered directly on the map
        validate(data, errRecorder);
    }

    /**
     * Always returns one because the child type is the element type.
     */
    @Override
    public int getChildTypeCount() {
        return 1;
    }

    /**
     * Returns the name of the map element type.
     */
    @Override
    public Collection<String> getChildrenNames() {
        return Collections.singletonList(MAP_CHILD_NAME);
    }

    /**
     * Returns <code>true</code> if the specified name is the name of a field.
     */
    @Override
    public boolean hasChild(final String name) {
        return MAP_CHILD_NAME.equals(name);
    }

    /**
     * Creates an empty map and wraps it in data setter.
     */
    @Override
    public DataSetter newValue() throws MetadataException {
        final Object map = newInstance();
        return new EditableDataObject(this, map);
    }

    /**
     * Returns a deep copy of the specified data.
     * 
     * @param data the data object to copy
     * @return a deep copy of the specified data
     * @throws MetadataException thrown when there is an error making a copy
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Object deepCopy(final Object data) throws MetadataException {
        final Map<String, Object> copy = (Map<String, Object>) newInstance();
        final Map<String, Object> orig = safeCast(data);
        final AbstractDataType fieldType = getChildTypeFromPathPart(null);
        for (final Map.Entry<String, Object> entry : orig.entrySet()) {
            copy.put(entry.getKey(), fieldType.deepCopy(entry.getValue()));
        }
        return copy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.worthent.foundation.service.metadata.DataType#deepCopy()
     */
    @Override
    public DataType deepCopy() {
        return new MapType(this);
    }

    /**
     * Returns the names of the keys in the specified data object
     */
    @Override
    protected Collection<String> getMapElementNames(final Object data)
        throws MetadataException {
        final Map<String, Object> map = safeCast(data);
        return map.keySet();
    }

    //
    // Overrides to AbstractMapType
    //

    @Override
    protected void assertValidElementName(final String fieldName)
        throws MetadataException {
        if (StringUtils.isBlank(fieldName)) {
            throw new MetadataException("The specified element name, '" +
                fieldName +
                "', is null or blank.");
        }
    }

    @Override
    protected AbstractDataType getChildTypeFromPathPart(final String fieldName) {
        // The element type is the same for every field
        return elementType;
    }

}
