package com.worthent.foundation.util.metadata.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.worthent.foundation.util.metadata.Converter;
import com.worthent.foundation.util.metadata.ConverterType;
import com.worthent.foundation.util.metadata.DataGetter;
import com.worthent.foundation.util.metadata.DataSetter;
import com.worthent.foundation.util.metadata.DataType;
import com.worthent.foundation.util.metadata.DataTypeFactory;
import com.worthent.foundation.util.metadata.DictionaryReference;
import com.worthent.foundation.util.metadata.MetadataException;
import com.worthent.foundation.util.metadata.MetadataFactory;
import com.worthent.foundation.util.metadata.NamedType;
import com.worthent.foundation.util.metadata.TypeCode;
import com.worthent.foundation.util.metadata.TypeDictionary;
import com.worthent.foundation.util.metadata.Validator;

/**
 * Provides a metadata description of the data structure used to persist data types.
 * 
 * @author Erik K. Worth
 */
public class MetadataMetadata {

    /** Shortcut to dictionary store holding type dictionaries */
    private static final DictionaryStore DICT_STORE =
        DictionaryStore.getInstance();

    /** Shortcut for the null string constant */
    private static final String NULL_STRING = "null";

    /**
     * Used to indicate a local dictionary reference context. This is used when
     * type references are going to be internalized in the same VM from which
     * they were externalized.
     */
    static final String LOCAL_DICT_REF_CONTEXT = "LocalDictRefContext";

    /** The ID of the type metadata in the type metadata dictionary */
    private static final String TYPE_STATE_TYPE_ID = "TypeStateType";

    /** The name of the field holding the dictionary ID */
    private static final String DICT_ID = "DictId";

    /** The name of the field that persists the type code */
    private static final String TYPE_CODE = "TypeCode";

    /** The name of the field that persists the attributes */
    private static final String ATTRS = "Attrs";

    /** The name of the field that persists type reference state data */
    static final String DICT_REF_TYPE = "RefType";

    /** The name of the field holding the child type name */
    private static final String CHILD_NAME = "ChildName";

    /** The name of the field holding the child type type definition */
    private static final String CHILD_TYPE = "ChildType";

    /** The name of the field holding the default value for the child type */
    // private static final String DEFAULT_VALUE = "DefaultValue";

    /** The name of the field holding the list of child types */
    private static final String CHILD_TYPES = "ChildTypes";

    /** The path to the class attribute in the data object */
    private static final String CLASS_ATTR_PATH = ATTRS + '.' + DataType.CLASS;

    /** The path to the choices attribute in the data object */
    private static final String CHOICES_ATTR_PATH =
        ATTRS + '.' + DataType.CHOICES;

    /** The path to the reference type ID attribute in the data object */
    private static final String REF_TYPE_ID_PATH =
        ATTRS + '.' + DataType.REF_TYPE_ID;

    /** Shortcut for Data Type Factory */
    private static final DataTypeFactory FACTORY =
        MetadataFactory.getInstance();

    /** Default type dictionary */
    private static final TypeDictionary DEFAULT_DICT =
        MetadataFactory.getDefaultDictionary();

    /** The type metadata dictionary */
    private static final TypeDictionary METADATA_DICTIONARY =
        DEFAULT_DICT.newDictionary("TYPE_METADATA_DICTIONARY");

    /** String data type */
    private static final DataType STRING_TYPE =
        DEFAULT_DICT.getType(TypeDictionary.STRING);

    /** Type Definition for a list of strings */
    private static final DataType STRING_LIST_TYPE =
        FACTORY.declareList(ArrayList.class.getName(), STRING_TYPE);

    /** Type definition for the enumeration of type codes */
    private static final DataType TYPE_CODE_TYPE =
        FACTORY.declareEnumeration(TypeCode.values());

    /** The type definition for the type attributes known to the base class */
    private static final DataType BASE_ATTRS_TYPE =
        FACTORY.declareStruct(
            new NamedType(DataType.CLASS, STRING_TYPE),
            new NamedType(DataType.CHOICES, STRING_LIST_TYPE),
            new NamedType(DataType.REF_TYPE_ID, STRING_TYPE));

    /** The type definition for the state of a dictionary reference */
    static DataType DICT_REF_STATE_TYPE =
        FACTORY.declareStruct(
            new NamedType(AbstractDictionaryReference.FACTORY_CLASS_NAME, STRING_TYPE),
            new NamedType(AbstractDictionaryReference.LOCAL_DICT_REF, STRING_TYPE),
            new NamedType(AbstractDictionaryReference.REMOTE_DICT_REF, STRING_TYPE));

    /** Forward reference to the type metadata data type definition */
    private static final DataType TYPE_STATE_REF_TYPE =
        FACTORY.declareReference(TYPE_STATE_TYPE_ID, METADATA_DICTIONARY);

    /** The type definition for a child type */
    private static final DataType CHILD_TYPE_DEF =
        FACTORY.declareStruct(
            new NamedType(CHILD_NAME, STRING_TYPE),
            new NamedType(CHILD_TYPE, TYPE_STATE_REF_TYPE));

    /** List of child type definitions */
    private static final DataType CHILD_TYPE_LIST =
        FACTORY.declareList(LinkedList.class.getName(), CHILD_TYPE_DEF);

    /** The state of the data type definition */
    private static final DataType TYPE_STATE_TYPE =
        FACTORY.declareStruct(
            new NamedType(TYPE_CODE, TYPE_CODE_TYPE),
            new NamedType(ATTRS, BASE_ATTRS_TYPE),
            new NamedType(DICT_REF_TYPE, DICT_REF_STATE_TYPE),
            new NamedType(CHILD_TYPES, CHILD_TYPE_LIST));

    /** The state definition for the type dictionary */
    static final DataType TYPE_DICT_TYPE =
        FACTORY.declareStruct(
            new NamedType(DICT_ID, STRING_TYPE),
            new NamedType(CHILD_TYPES, CHILD_TYPE_LIST));

    /** Single instance of this class */
    private static final MetadataMetadata INSTANCE = new MetadataMetadata();

    /** @return the single instance of this class */
    public static MetadataMetadata getInstance() {
        return INSTANCE;
    }

    /**
     * @return the metadata describing the data object used to hold the state of
     * a type definition.
     */
    public DataType getStateMetadata() {
        return TYPE_STATE_TYPE;
    }

    /** @return the type code from the data object */
    TypeCode getTypeCode(final DataGetter dataObject) throws MetadataException {
        final String typeCodeStr = dataObject.get(TYPE_CODE);
        return TypeCode.valueOf(typeCodeStr);
    }

    /**
     * Returns the map of attributes from the data object
     *
     * @param dataObject the data object
     * @return the map of attributes from the data object
     */
    Map<String, Object> getAttrs(final DataGetter dataObject)
        throws MetadataException {
        final Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(DataType.CLASS, dataObject.get(CLASS_ATTR_PATH));
        final List<String> choices = dataObject.get(CHOICES_ATTR_PATH);
        if ((null != choices) && (choices.size() > 0)) {
            final String[] choiceArray = new String[choices.size()];
            choices.toArray(choiceArray);
            attrs.put(DataType.CHOICES, choiceArray);
        }
        final String refTypeId = dataObject.get(REF_TYPE_ID_PATH);
        if ((null != refTypeId) &&
            !refTypeId.isEmpty() &&
            !NULL_STRING.equals(refTypeId)) {
            attrs.put(DataType.REF_TYPE_ID, refTypeId);
        }
        return attrs;
    }

    /**
     * Returns the list of validators from the data object
     *
     * @param dataObject the data object
     * @return the list of validators from the data object
     */
    List<Validator> getValidators(final DataGetter dataObject)
        throws MetadataException {
        return new LinkedList<>();
    }

    /**
     * Returns the map of converters from the data object
     *
     * @param dataObject the data object
     * @return the map of converters from the data object
     */
    Map<ConverterType, Converter> getConverters(final DataGetter dataObject)
        throws MetadataException {
        return new HashMap<>();
    }

    /**
     * Returns the element type from the data object
     *
     * @param dataObject the data object
     * @return the element type from the data object
     */
    AbstractDataType getElementType(final DataGetter dataObject)
        throws MetadataException {
        final List<NamedType> childTypes = getChildTypes(dataObject);
        if (childTypes.size() != 1) {
            throw new MetadataException(
                "Expected one child type in data object but found instead, " +
                    childTypes.size());
        }
        final DataType elementDataType = childTypes.get(0).getType();
        AbstractDataType.assertIsClass(elementDataType, AbstractDataType.class);
        return (AbstractDataType) elementDataType;
    }

    /**
     * Returns the child type definitions from the data object
     * 
     * @param dataObject the data object holding the persistent state of the
     *        type definition
     * @return the child type definitions from the data object
     * @throws MetadataException thrown when there is an error retrieving the
     *         child type definitions from the data object
     */
    List<NamedType> getChildTypes(final DataGetter dataObject)
        throws MetadataException {
        final List<Object> children = dataObject.get(CHILD_TYPES);
        final int size = (children == null) ? 0 : children.size();
        final List<NamedType> childTypes = new ArrayList<NamedType>(size);
        final DataSetter child = CHILD_TYPE_DEF.newValue();
        for (int i = 0; i < size; i++) {
            // Wrap the child in a data object
            child.set(children.get(i));
            final String name = child.get(CHILD_NAME);
            final DataGetter childTypeDataObject =
                child.getDataGetter(CHILD_TYPE);
            final DataType type =
                FACTORY.restoreFromStateObject(childTypeDataObject);
            // TODO: Support initial value
            Object initialValue = null;
            final NamedType namedType = new NamedType(name, type, initialValue);
            childTypes.add(namedType);
        }
        return childTypes;
    }

    /**
     * Returns the local dictionary reference from the data object
     *
     * @param dataObject the data object
     * @return the local dictionary reference from the data object
     */
    public String getLocalDictionaryReference(final DataGetter dataObject)
        throws MetadataException {
        final String localDictRef =
            dataObject.get(AbstractDictionaryReference.LOCAL_DICT_REF);
        if (null == localDictRef) {
            throw new IllegalStateException("null value for '" +
                AbstractDictionaryReference.LOCAL_DICT_REF +
                "' in data object");
        }
        return localDictRef;
    }

    /**
     * Returns the remote dictionary reference from the data object
     *
     * @param dataObject the data object
     * @return the remote dictionary reference from the data object
     */
    public String getRemoteDictionaryReference(final DataGetter dataObject)
        throws MetadataException {
        final String remoteDictRef =
            dataObject.get(AbstractDictionaryReference.REMOTE_DICT_REF);
        if (null == remoteDictRef) {
            throw new IllegalStateException("null value for '" +
                AbstractDictionaryReference.REMOTE_DICT_REF +
                "' in data object");
        }
        return remoteDictRef;
    }

    /**
     * Returns the dictionary reference from the data object
     *
     * @param dataObject the data object
     * @return the dictionary reference from the data object
     */
    DictionaryReference getDictionaryReference(final DataGetter dataObject)
        throws MetadataException {
        return AbstractDictionaryReferenceFactory.getInstance().restore(
            dataObject);
    }

    /**
     * Returns the type definition's state in a data object.
     *
     * @param type the type definition
     * @return the type definition's state in a data object
     */
    DataGetter populateTypeState(final AbstractDataType type)
        throws MetadataException {
        return this.populateTypeState(type, LOCAL_DICT_REF_CONTEXT);
    }

    /**
     * Returns the type definition's state in a data object.
     * 
     * @param type the type declaration to capture as a data object
     * @param dictionaryContext this given the value,
     *        {@link #LOCAL_DICT_REF_CONTEXT} when the type being captured is to
     *        be restored in the same VM or if it is being passed to another VM,
     *        it is the Service ID of the Service able restore the type remotely
     * @return the type definition's state in a data object
     */
    DataGetter populateTypeState(
        final AbstractDataType type,
        final String dictionaryContext) throws MetadataException {
        final DataSetter dataObject = TYPE_STATE_TYPE.newValue();
        final TypeCode typeCode =
            type.isReference() ? TypeCode.REFERENCE : type.getTypeCode();
        dataObject.set(TYPE_CODE, typeCode.name());
        dataObject.set(CLASS_ATTR_PATH, type.getAttribute(DataType.CLASS));
        final List<String> choiceAttr = type.getAttribute(DataType.CHOICES);
        if (null != choiceAttr) {
            dataObject.set(CHOICES_ATTR_PATH, choiceAttr);
        }
        if (type.isReference()) {
            // Make sure the class is not for the referenced type
            dataObject.set(CLASS_ATTR_PATH, Object.class.getName());
            final String typeId = ((RefType) type).getTypeId();
            dataObject.set(REF_TYPE_ID_PATH, typeId);
            // Persist dictionary reference information
            final DictionaryReference dictRef =
                ((RefType) type).getDictionaryReference();
            final DataSetter refData = DICT_REF_STATE_TYPE.newValue();
            String factoryClass = null;
            if (LOCAL_DICT_REF_CONTEXT.equals(dictionaryContext)) {
                factoryClass =
                    LocalDictionaryReferenceFactory.class
                        .getName();
            } else {
                factoryClass =
                    RemoteDictionaryReference.RemoteDictionaryReferenceFactory.class
                        .getName();
            }
            refData.set(
                AbstractDictionaryReference.FACTORY_CLASS_NAME,
                factoryClass);
            refData.set(AbstractDictionaryReference.LOCAL_DICT_REF, dictRef
                .getLocalReferenceId());
            refData.set(
                AbstractDictionaryReference.REMOTE_DICT_REF,
                dictionaryContext);
            dataObject.set(DICT_REF_TYPE, refData);
            // Make sure the dictionary is available when this reference is
            // internalized
            final TypeDictionary dict = dictRef.getDictionary();
            final String dictId = dict.getId();
            if (!DICT_STORE.hasDictionary(dictId)) {
                DICT_STORE.put(dictId, dict);
            }

        } else if (!type.isSimpleType()) {
            for (final String childName : type.getChildrenNames()) {
                final DataType childType = type.getChildType(childName);
                AbstractDataType.assertIsClass(
                    childType,
                    AbstractDataType.class);
                final DataSetter childTypeData = CHILD_TYPE_DEF.newValue();
                childTypeData.set(CHILD_NAME, childName);
                childTypeData.set(CHILD_TYPE, populateTypeState(
                    (AbstractDataType) childType,
                    dictionaryContext));
                // TODO: Support initial values
                dataObject.addElement(CHILD_TYPES, childTypeData);
            }
        }
        return dataObject;
    }

    /**
     * Returns a data object holding the state of a type dictionary excluding
     * the types declared in the default dictionary.
     * 
     * @param dict the type dictionary to externalize to a data object
     * @return a data object holding the state of a type dictionary excluding
     *         the types declared in the default dictionary
     * @throws MetadataException thrown when there is an error populating the
     *         data object from the type definitions in the dictionary
     */
    DataGetter populateDictionaryState(final TypeDictionary dict)
        throws MetadataException {
        final DataSetter dataObj = TYPE_DICT_TYPE.newValue();
        dataObj.set(DICT_ID, dict.getId());
        for (final String typeId : dict.getTypeIds()) {
            final AbstractDataType childType =
                (AbstractDataType) dict.getType(typeId);
            final DataSetter childTypeData = CHILD_TYPE_DEF.newValue();
            childTypeData.set(CHILD_NAME, typeId);
            childTypeData.set(CHILD_TYPE, populateTypeState(childType));
            dataObj.addElement(CHILD_TYPES, childTypeData);
        }
        return dataObj;
    }

    /**
     * Returns a type dictionary created using the type definitions internalized
     * from the specified data object.
     * 
     * @param parent the parent dictionary of the new dictionary. If this is
     *        <code>null</code> it uses the default dictionary as the parent.
     * @param dataObj the data object holding the externalized state of a type
     *        dictionary
     * @return a type dictionary created using the type definitions internalized
     *         from the specified data object
     * @throws MetadataException thrown when there is an error reconstructing
     *         the type definitions from the data object
     */
    TypeDictionary getTypeDictionary(
        final TypeDictionary parent,
        final DataGetter dataObj) throws MetadataException {
        final String dictId = dataObj.get(DICT_ID);
        if (null == dictId) {
            throw new MetadataException(
                "Expected the data object to have a value for field, " +
                    DICT_ID);
        }
        final TypeDictionary root = (null == parent) ? DEFAULT_DICT : parent;
        final TypeDictionary dict = root.newDictionary(dictId);
        if (!DICT_STORE.hasDictionary(dictId)) {
            DICT_STORE.put(dictId, dict);
        }
        final List<NamedType> types = getChildTypes(dataObj);
        for (final NamedType type : types) {
            dict.putType(type.getName(), type.getType());
        }
        return dict;
    }

    private MetadataMetadata() {
        METADATA_DICTIONARY.putType(TYPE_STATE_TYPE_ID, TYPE_STATE_TYPE);
    }

}
