package com.worthent.foundation.util.metadata.internal;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import com.worthent.foundation.util.metadata.DataGetter;
import com.worthent.foundation.util.metadata.DataSetter;
import com.worthent.foundation.util.metadata.DataType;
import com.worthent.foundation.util.metadata.DictionaryReference;
import com.worthent.foundation.util.metadata.MetadataException;
import com.worthent.foundation.util.metadata.TypeCode;
import com.worthent.foundation.util.metadata.TypeDictionary;
import com.worthent.foundation.util.recorder.DataErrorRecorder;

/**
 * A type definition that references another type definition in a dictionary.
 * 
 * @author Erik K. Worth
 */
public class RefType extends AbstractDataType {

    /** Serial Version ID */
    private static final long serialVersionUID = 1042945300970549922L;

    /** The object able to locate a dictionary */
    private DictionaryReference dictRef;
    
    /** Used for internalization purposes only */
    @SuppressWarnings("unused")
    private RefType() {
        dictRef = null;
    }

    /**
     * Construct with a reference to a dictionary and the ID of the type
     * definition in that dictionary
     * 
     * @param typeId unique ID of the type in the dictionary
     * @param dictRef reference to a dictionary
     */
    RefType(final String typeId, final DictionaryReference dictRef) {
        super(TypeCode.REFERENCE, Object.class.getName());
        if (null == dictRef) {
            throw new IllegalArgumentException("null dictRef");
        }
        if (null == typeId) {
            throw new IllegalArgumentException("null typeId");
        }
        this.dictRef = dictRef;
        setAttribute(REF_TYPE_ID, typeId);
    }

    /**
     * Copy constructor.
     */
    private RefType(final RefType other) {
        super(other);
        this.dictRef = other.dictRef;
    }

    /**
     * Construct from a state data object
     *
     * @param dataObject the data object holding the information used to create the type definition
     */
    protected RefType(final DataGetter dataObject) throws MetadataException {
        super(dataObject);
        final MetadataMetadata metaMeta = MetadataMetadata.getInstance();
        final DataGetter dictRefData =
            dataObject.getDataGetter(MetadataMetadata.DICT_REF_TYPE);
        this.dictRef = metaMeta.getDictionaryReference(dictRefData);
    }

    /**
     * Returns <code>true</code> if the other type is the same reference or the
     * referenced type is the same as the other type.
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RefType)) {
            final DataType type = getReferencedType();
            return type.equals(other);
        }
        RefType that = (RefType) other;
        return Objects.equals(dictRef, that.dictRef) &&
                Objects.equals(getTypeId(), that.getTypeId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(dictRef, getTypeId());
    }

    /** Returns the dictionary reference */
    DictionaryReference getDictionaryReference() {
        return dictRef;
    }

    /**
     * This is set when a new dictionary is created from a parent dictionary
     * that contains a type that references the parent dictionary. The type is
     * copied to the new dictionary and the reference is updated to refer to the
     * new dictionary.
     * 
     * @param dictRef the new dictionary reference
     */
    void setDictionaryReference(final DictionaryReference dictRef) {
        this.dictRef = dictRef;
    }

    @Override
    protected Object deepCopy(final Object data) throws MetadataException {
        final AbstractDataType type = getReferencedTypeChecked();
        return type.deepCopy(data);
    }

    @Override
    protected Path newPath(final String path) throws MetadataException {
        final AbstractDataType type = getReferencedTypeChecked();
        return type.newPath(path);
    }

    @Override
    public void assertValid() throws MetadataException {
        // This type should be able to access the referenced type
        getReferencedTypeChecked();
    }

    @Override
    public DataType deepCopy() {
        return new RefType(this);
    }

    @Override
    public void assertValid(final Object value) throws MetadataException {
        getReferencedTypeChecked().assertValid(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAttribute(final String name) throws MetadataException {
        final Object attr = getReferencedTypeChecked().getAttribute(name);
        return (T) attr;
    }

    @Override
    public Set<String> getAttributeNames() {
        return getReferencedType().getAttributeNames();
    }

    @Override
    public DataType getChildType(final String name) throws MetadataException {
        return getReferencedTypeChecked().getChildType(name);
    }

    @Override
    public int getChildTypeCount() {
        return getReferencedType().getChildTypeCount();
    }

    @Override
    public Collection<String> getChildrenNames() {
        return getReferencedType().getChildrenNames();
    }

    @Override
    public TypeCode getTypeCode() {
        return getReferencedType().getTypeCode();
    }

    @Override
    public boolean hasAttribute(final String name) {
        return getReferencedType().hasAttribute(name);
    }

    @Override
    public boolean hasChild(final String name) {
        return getReferencedType().hasChild(name);
    }

    @Override
    public boolean hasInitialValue() {
        return getReferencedType().hasInitialValue();
    }

    @Override
    public boolean isReference() {
        return true;
    }

    @Override
    public boolean isSimpleType() {
        return getReferencedType().isSimpleType();
    }

    @Override
    public DataSetter newValue() throws MetadataException {
        // Prevent stack overflow when there is a recursive structure
        return null;
    }

    @Override
    protected String getNextPathPart(final Path path) throws MetadataException {
        final AbstractDataType type = getReferencedTypeChecked();
        return type.getNextPathPart(path);
    }

    @Override
    protected String getRemainingPath(final Path path) {
        final AbstractDataType type = getReferencedType();
        return type.getRemainingPath(path);
    }

    @Override
    protected AbstractDataType getChildTypeFromPathPart(final String pathPart)
        throws MetadataException {
        final AbstractDataType type = getReferencedTypeChecked();
        return type.getChildTypeFromPathPart(pathPart);
    }

    @Override
    protected Object getChildValueFromPathPart(
        final String pathPart,
        final Object data) throws MetadataException {
        final AbstractDataType type = getReferencedTypeChecked();
        return type.getChildValueFromPathPart(pathPart, data);
    }

    @Override
    protected void addElement(final Object data, final Object element)
        throws MetadataException {
        final AbstractDataType type = getReferencedTypeChecked();
        type.addElement(data, element);
    }

    @Override
    protected void addElement(final Object data) throws MetadataException {
        final AbstractDataType type = getReferencedTypeChecked();
        type.addElement(data);
    }

    @Override
    protected Collection<String> getMapElementNames(final Object data)
        throws MetadataException {
        final AbstractDataType type = getReferencedTypeChecked();
        return type.getMapElementNames(data);
    }

    @Override
    protected void setItem(
        final String item,
        final Object data,
        final Object element) throws MetadataException {
        final AbstractDataType type = getReferencedTypeChecked();
        type.setItem(item, data, element);
    }

    /** @return  the ID of the type in the dictionary */
    public final String getTypeId() {
        try {
            return super.getAttribute(REF_TYPE_ID);
        } catch (final MetadataException exc) {
            throw new IllegalStateException(
                "Reference type missing type ID attribute");
        }
    }

    /**
     * @return  the data type referenced by this type reference.
     * 
     * @throws IllegalStateException thrown when there is an error retrieving
     *         the data type through the reference
     */
    final AbstractDataType getReferencedType() {
        final String typeId;
        try {
            typeId = super.getAttribute(REF_TYPE_ID);
        } catch (final MetadataException exc) {
            throw new IllegalStateException(
                "Reference type missing type ID attribute");
        }
        try {
            return getReferencedTypeChecked();
        } catch (final MetadataException exc) {
            throw new IllegalStateException(
                "Error fetching referenced type, '" + typeId + "'",
                exc);
        }
    }

    /**
     * @return  the data type referenced by this type reference.
     */
    public final AbstractDataType getReferencedTypeChecked()
        throws MetadataException {
        final String typeId = super.getAttribute(REF_TYPE_ID);
        final TypeDictionary dict = dictRef.getDictionary();
        final DataType type = dict.getType(typeId);
        if (null == type) {
            throw new MetadataException("No such type in dictionary + '" +
                typeId +
                "'");
        }
        assertIsClass(type, AbstractDataType.class);
        return (AbstractDataType) type;
    }
}
