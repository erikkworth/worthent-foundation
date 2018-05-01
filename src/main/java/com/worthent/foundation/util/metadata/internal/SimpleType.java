package com.worthent.foundation.util.metadata.internal;

import java.lang.reflect.Constructor;
import java.text.Format;
import java.util.LinkedHashMap;
import java.util.Map;

import com.worthent.foundation.util.lang.StringUtils;
import com.worthent.foundation.util.metadata.DataGetter;
import com.worthent.foundation.util.metadata.DataSetter;
import com.worthent.foundation.util.metadata.DataType;
import com.worthent.foundation.util.metadata.MetadataException;
import com.worthent.foundation.util.metadata.TypeCode;

/**
 * Represents a simple data type.
 * 
 * @author Erik K. Worth
 */
public class SimpleType extends AbstractDataType {

    /** Serial Version ID */
    private static final long serialVersionUID = 384688548468164230L;

    /** Definition of the java BigDecimal type. */
    static final SimpleType BIG_DECIMAL =
        new SimpleType(TypeCode.NUMERIC, java.math.BigDecimal.class.getName());

    /** Definition of the java BigInteger type. */
    static final SimpleType BIG_INTEGER =
        new SimpleType(TypeCode.NUMERIC, java.math.BigInteger.class.getName());

    /** Definition of the java Boolean type. */
    static final SimpleType BOOLEAN =
        new SimpleType(TypeCode.BOOLEAN, Boolean.class.getName());

    /** Definition of the java Byte type. */
    static final SimpleType BYTE =
        new SimpleType(TypeCode.NUMERIC, Byte.class.getName());

    /** Definition of the java Double type. */
    static final SimpleType DOUBLE =
        new SimpleType(TypeCode.NUMERIC, Double.class.getName());

    /** Definition of the java Integer type. */
    static final SimpleType INTEGER =
        new SimpleType(TypeCode.NUMERIC, Integer.class.getName());

    /** Definition of the java Float type. */
    static final SimpleType FLOAT =
        new SimpleType(TypeCode.NUMERIC, Float.class.getName());

    /** Definition of the java Long type. */
    static final SimpleType LONG =
        new SimpleType(TypeCode.NUMERIC, Long.class.getName());

    /** Definition of the java Short type. */
    static final SimpleType SHORT =
        new SimpleType(TypeCode.NUMERIC, Short.class.getName());

    /** Definition of the java String type. */
    static final SimpleType STRING =
        new SimpleType(TypeCode.STRING, String.class.getName());

    private static final Map<Class<?>, Object> SIMPLE_TYPE_DEFAULTS;

    static {
        SIMPLE_TYPE_DEFAULTS = new LinkedHashMap<>();
        SIMPLE_TYPE_DEFAULTS.put(String.class, "");
    }
    
    /** Used for internalization purposes only */
    protected SimpleType() {
        super();
    }

    /**
     * Construct with a type code.
     * 
     * @param typeCode the type code identifying the basic type of data this class represents
     * @param javaClassName the fully qualified name of the Java class backing this type
     */
    protected SimpleType(final TypeCode typeCode, final String javaClassName) {
        super(typeCode, javaClassName);
    }

    /**
     * Copy Constructor.
     * 
     * @param other the other simple type to copy
     */
    protected SimpleType(final SimpleType other) {
        super(other);
    }
    
    /**
     * Construct from state data object
     *
     * @param dataObject the data object holding the information used to define this type
     */
    protected SimpleType(final DataGetter dataObject) throws MetadataException {
        super(dataObject);
    }

    /**
     * Throws <code>MetadataException</code> if the type has not been properly
     * defined.
     * 
     * @throws MetadataException when the type has not been property defined
     */
    public void assertValid() throws MetadataException {
        // Simple types does not need validation at this point
    }

    @Override
    public DataType deepCopy() {
        return new SimpleType(this);
    }
    
    /**
     * This implementation always returns <code>true</code>.
     */
    @Override
    public boolean isSimpleType() {
        return true;
    }

    /**
     * This implementation always returns <code>null</code>
     */
    @Override
    public DataSetter newValue() throws MetadataException {
        return new EditableDataObject(this, null);
    }

    /**
     * Return the string representation of the specified data object.
     * 
     * @param data the data object
     * @param format the format to use or <code>null</code> to use the default
     *        string representation
     * @return the string representation of the specified data object
     */
    @Override
    protected String asString(final Object data, final Format format) 
    throws MetadataException {
        if (null == data) {
            return null;
        }
        if (null == format) {
            return data.toString();
        }
        return format.format(data);
    }

    /**
     * Creates an instance of the object using its String constructor.
     * 
     * @param value the string value for the object
     * @return a new instance of the object using its String constructor
     * @throws MetadataException thrown when there is an error constructing an
     *         instance of the object using its string constructor
     */
    @Override
    protected Object fromString(final String value) throws MetadataException {
        if (null == value) {
            return null;
        }
        final Class<?> javaClass = getJavaClass();
        try {
            // Find the string constructor
            final Constructor<?> constructor =
                javaClass.getConstructor(String.class);
            return constructor.newInstance(value);
        } catch (final Exception exc) {
            throw new MetadataException("Error creating an object of type, '" +
                getTypeCode() +
                "', using the String contructor for the class, '" +
                javaClass.getName() +
                "'", exc);
        }
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
        // The simple types are all immutable, so there is no need to copy.
        return data;
    }

    @Override
    protected Path newPath(String path) throws MetadataException {
        if (StringUtils.isNotBlank(path)) {
            throw new MetadataException("The type, '" +
                getTypeCode() +
                "', has no elements within identified by the path, '" +
                path +
                "'");
        }
        return null;
    }

    @Override
    protected Object newInstance() throws MetadataException {
        final Class<?> javaClass = getJavaClass();
        final Object obj;
        if (TypeCode.DATE.equals(getTypeCode())) {
            obj = super.newInstance();
        } else {
            obj = SIMPLE_TYPE_DEFAULTS.get(javaClass);
        }
        return obj;
    }
}
