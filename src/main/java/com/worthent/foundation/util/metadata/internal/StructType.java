package com.worthent.foundation.util.metadata.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.worthent.foundation.util.lang.StringUtils;

import com.worthent.foundation.util.metadata.DataGetter;
import com.worthent.foundation.util.metadata.DataSetter;
import com.worthent.foundation.util.metadata.DataType;
import com.worthent.foundation.util.metadata.MetadataException;
import com.worthent.foundation.util.metadata.NamedType;
import com.worthent.foundation.util.metadata.TypeCode;
import com.worthent.foundation.util.recorder.DataErrorRecorder;

/**
 * Defines a struct data type.
 * 
 * @author Erik K. Worth
 */
public class StructType extends AbstractMapType {

    /** Serial Version ID */
    private static final long serialVersionUID = 997296766380515162L;

    /** Shortcut for the class name of the java class backing this data type */
    private static final String CLASS_NAME = LinkedHashMap.class.getName();

    /** The fields in this struct */
    private final Map<String, NamedType> fields;
    
    /** Used for internalization purposes only */
    @SuppressWarnings("unused")
    private StructType() {
        super();
        fields = null;
    }

    /**
     * Constructs a struct type from its fields
     * 
     * @param fieldList the fields specified for this struct
     */
    StructType(final List<NamedType> fieldList) {
        super(TypeCode.STRUCT, CLASS_NAME);
        this.fields = new LinkedHashMap<>();
        if (null != fieldList) {
            for (final NamedType field : fieldList) {
                final String fieldName = field.getName();
                if (!this.fields.containsKey(fieldName)) {
                    this.fields.put(field.getName(), field);
                }
            } // for each field
        }
    }

    /**
     * Copy constructor
     * @param other the struct to copy
     */
    private StructType(final StructType other) {
        super(other);
        fields = new LinkedHashMap<>();
        for (final Map.Entry<String, NamedType> entry: other.fields.entrySet()) {
            final NamedType copy = new NamedType(entry.getValue());
            fields.put(copy.getName(), copy);
        }
    }

    /**
     * Extend constructor
     *
     * @param other the struct to extend
     * @param fieldList the list of additional fields in the new type
     */
    StructType(final StructType other, final List<NamedType> fieldList) {
        this(other);
        if (null != fieldList) {
            for (final NamedType field : fieldList) {
                final String fieldName = field.getName();
                if (!this.fields.containsKey(fieldName)) {
                    this.fields.put(field.getName(), field);
                }
            } // for each field
        }
    }
    
    /**
     * Construct from a state data object
     * 
     * @param dataObject the data object holding the state of the enumerated
     *        type definition
     * @throws MetadataException thrown when there is an error creating the type
     *         from its state object
     */
    StructType(final DataGetter dataObject) throws MetadataException {
        super(dataObject);
        this.fields = new LinkedHashMap<>();
        final MetadataMetadata metaMeta = MetadataMetadata.getInstance();
        final List<NamedType> childTypes = metaMeta.getChildTypes(dataObject);
        for (final NamedType childType: childTypes) {
            fields.put(childType.getName(), childType);
        }
    }

    /**
     * Throws <code>MetadataException</code> if the type has not been properly
     * defined.
     * 
     * @throws MetadataException when the type has not been property defined
     */
    public void assertValid() throws MetadataException {
        if (TypeCode.STRUCT != getTypeCode()) {
            throw new MetadataException("Expected type code, " +
                TypeCode.STRUCT +
                ", but found instead, " +
                getTypeCode());
        }

        // Make sure the fields are in the map. A field might not be in
        // the map if it had the same name as another.
        for (final NamedType field : fields.values()) {
            if (field != fields.get(field.getName())) {
                throw new MetadataException("The field named, '" +
                    field.getName() +
                    "', is a duplicate of another field");
            }
        }
    }

    /**
     * Validates that the specified value is a <code>LinkedHashMap</code> with
     * fields that match the field definition.
     */
    public void assertValid(final Object value) throws MetadataException {
        super.assertValid(value);
        final LinkedHashMap<String, Object> map = safeCast(value);

        // Make sure all the fields in the provided map are defined in the
        // struct
        final Set<String> validatedFields = new HashSet<>();
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            final String name = entry.getKey();
            final NamedType field = fields.get(name);
            if (null == field) {
                throw new MetadataException("Unexpected field, '" + name + "'");
            }
            // Validate the field
            try {
                field.getType().assertValid(entry.getValue());
                validatedFields.add(name);
            } catch (final Exception exc) {
                throw new MetadataException("Invalid struct field, '" +
                    name +
                    "'", exc);
            }
        }

        // Make sure there are not missing required fields
        for (final Map.Entry<String, NamedType> entry : fields.entrySet()) {
            final String name = entry.getKey();
            if (!validatedFields.contains(name)) {
                final DataType fieldType = entry.getValue().getType();
                // Validate the field
                try {
                    fieldType.assertValid(map.get(name));
                } catch (final Exception exc) {
                    throw new MetadataException("Missing required field, '" +
                        name +
                        "'", exc);
                }
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
        final LinkedHashMap<String, Object> map = safeCast(data);

        // Validate all the fields in the struct
        if (null != map) {
            final String priorDataId = errRecorder.getDataId();
            try {
                String priorPath = (null == priorDataId) ? "" : priorDataId;
                if (StringUtils.isNotBlank(priorPath) &&
                    !priorPath.endsWith("]")) {
                    priorPath = priorPath + ".";
                }
                for (final Map.Entry<String, NamedType> entry : fields
                    .entrySet()) {
                    final String fieldName = entry.getKey();
                    final AbstractDataType fieldType =
                        getChildTypeFromPathPart(fieldName);
                    final Object fieldValue = map.get(fieldName);
                    final String newDataId = priorPath + fieldName;
                    errRecorder.setDataId(newDataId);
                    fieldType.deepValidate(fieldValue, errRecorder);
                }
            } finally {
                errRecorder.setDataId(priorDataId);
            }
        }

        // Run the validators registered directly on the struct
        validate(data, errRecorder);
    }

    /**
     * Returns the number of fields.
     */
    @Override
    public int getChildTypeCount() {
        return fields.size();
    }

    /**
     * Returns the list of field names in the order declared.
     */
    @Override
    public Collection<String> getChildrenNames() {
        final ArrayList<String> names = new ArrayList<>(fields.size());
        for (final NamedType fld : fields.values()) {
            names.add(fld.getName());
        }
        return Collections.unmodifiableCollection(names);
    }

    /**
     * Returns <code>true</code> if the specified name is the name of a field.
     */
    @Override
    public boolean hasChild(final String name) {
        return fields.containsKey(name);
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
        if (!(other instanceof StructType)) {
            return false;
        }
        final StructType that = (StructType) other;
        return fields.equals(that.fields) && super.equals(that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fields, super.hashCode());
    }

    @Override
    public DataType deepCopy() {
        return new StructType(this);
    }

    //
    // Overloads to AbstractDataType
    //

    /**
     * Returns the Java class from the Java Class Name attribute.
     * 
     * @return the Java class from the Java Class Name attribute
     * @throws MetadataException thrown when there is an error creating the
     *         class from the name
     */
    @Override
    protected Class<?> getJavaClass() throws MetadataException {
        return LinkedHashMap.class;
    }

    /**
     * Returns a new instance of struct.
     */
    @Override
    public DataSetter newValue() throws MetadataException {
        final LinkedHashMap<String, Object> struct = new LinkedHashMap<>();
        for (final NamedType field : fields.values()) {
            final String fieldName = field.getName();
            try {
                final AbstractDataType fieldType =
                    getChildTypeFromPathPart(fieldName);
                final Object fldData = field.getInitialValue();
                if (null != fldData) {
                    struct.put(fieldName, fieldType.deepCopy(fldData));
                } else if (field.getType().hasInitialValue()) {
                    final DataSetter value = fieldType.newValue();
                    if (null != value) {
                        struct.put(fieldName, value.get());
                    }
                }
            } catch (final Exception exc) {
                throw new MetadataException(
                    "Error creating a value for field, '" + fieldName + "'",
                    exc);
            }
        }
        return new EditableDataObject(this, struct);
    }

    /**
     * Returns a deep copy of the specified data.
     * 
     * @param data the data object to copy
     * @return a deep copy of the specified data
     * @throws MetadataException thrown when there is an error making a copy
     */
    @Override
    protected Object deepCopy(final Object data) throws MetadataException {
        if (null == data) {
            return null;
        }
        final LinkedHashMap<String, Object> map = safeCast(data);

        final LinkedHashMap<String, Object> copy =
            new LinkedHashMap<>();
        for (final NamedType field : fields.values()) {
            final String fieldName = field.getName();
            try {
                final Object fldData = map.get(fieldName);
                if (null != fldData) {
                    final AbstractDataType fieldType =
                        getChildTypeFromPathPart(fieldName);
                    copy.put(fieldName, fieldType.deepCopy(fldData));
                }
            } catch (final Exception exc) {
                throw new MetadataException(
                    "Error creating a copy of field, '" + fieldName + "'",
                    exc);
            }
        }
        return copy;
    }

    /**
     * Returns the names of the fields in the order they were declared.
     */
    @Override
    protected Collection<String> getMapElementNames(final Object data)
        throws MetadataException {
        final ArrayList<String> names = new ArrayList<>(fields.size());
        for (final NamedType field : fields.values()) {
            names.add(field.getName());
        }
        return names;
    }

    //
    // Overrides to AbstractMapType
    //

    /**
     * Returns <code>true</code> when the field name is the name of one of the
     * pre-defined fields.
     */
    @Override
    protected void assertValidElementName(final String fieldName)
        throws MetadataException {
        if ((null == fieldName) || !fields.containsKey(fieldName)) {
            throw new MetadataException("The specified element name, '" +
                fieldName +
                "', does not identify a field in the struct.");
        }
    }

    /**
     * Returns the definition of the field for the specified field or
     * <code>null</code> if no field has been defined by that name.
     */
    @Override
    protected AbstractDataType getChildTypeFromPathPart(final String fieldName) {
        return (AbstractDataType) fields.get(fieldName).getType();
    }
    
}
